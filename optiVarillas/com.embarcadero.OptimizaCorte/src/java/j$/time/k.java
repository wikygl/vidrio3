package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.time.chrono.InterfaceC0484b;
import j$.time.chrono.InterfaceC0487e;
import j$.time.chrono.InterfaceC0493k;
import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class k implements j$.time.temporal.m, j$.time.temporal.p, InterfaceC0487e, Serializable {

    /* renamed from: c  reason: collision with root package name */
    public static final k f3977c = L(i.f3971d, m.f3983e);

    /* renamed from: d  reason: collision with root package name */
    public static final k f3978d = L(i.f3972e, m.f);
    private static final long serialVersionUID = 6207766400415563566L;

    /* renamed from: a  reason: collision with root package name */
    private final i f3979a;

    /* renamed from: b  reason: collision with root package name */
    private final m f3980b;

    private k(i iVar, m mVar) {
        this.f3979a = iVar;
        this.f3980b = mVar;
    }

    private int D(k kVar) {
        int D4 = this.f3979a.D(kVar.f3979a);
        return D4 == 0 ? this.f3980b.compareTo(kVar.f3980b) : D4;
    }

    public static k E(j$.time.temporal.o oVar) {
        if (oVar instanceof k) {
            return (k) oVar;
        }
        if (oVar instanceof E) {
            return ((E) oVar).I();
        }
        if (oVar instanceof s) {
            return ((s) oVar).G();
        }
        try {
            return new k(i.F(oVar), m.F(oVar));
        } catch (C0482c e4) {
            String name = oVar.getClass().getName();
            throw new RuntimeException("Unable to obtain LocalDateTime from TemporalAccessor: " + oVar + " of type " + name, e4);
        }
    }

    public static k K(int i4) {
        return new k(i.O(i4, 12, 31), m.K(0));
    }

    public static k L(i iVar, m mVar) {
        Objects.requireNonNull(iVar, "date");
        Objects.requireNonNull(mVar, "time");
        return new k(iVar, mVar);
    }

    public static k M(long j4, int i4, B b4) {
        long J3;
        long j5;
        Objects.requireNonNull(b4, "offset");
        long j6 = i4;
        j$.time.temporal.a.NANO_OF_SECOND.D(j6);
        return new k(i.Q(j$.com.android.tools.r8.a.n(j4 + b4.J(), 86400)), m.L((((int) j$.com.android.tools.r8.a.m(J3, j5)) * 1000000000) + j6));
    }

    private k P(i iVar, long j4, long j5, long j6, long j7) {
        m mVar = this.f3980b;
        if ((j4 | j5 | j6 | j7) == 0) {
            return T(iVar, mVar);
        }
        long j8 = j4 / 24;
        long j9 = j8 + (j5 / 1440) + (j6 / 86400) + (j7 / 86400000000000L);
        long j10 = 1;
        long j11 = ((j4 % 24) * 3600000000000L) + ((j5 % 1440) * 60000000000L) + ((j6 % 86400) * 1000000000) + (j7 % 86400000000000L);
        long T3 = mVar.T();
        long j12 = (j11 * j10) + T3;
        long n4 = j$.com.android.tools.r8.a.n(j12, 86400000000000L) + (j9 * j10);
        long m4 = j$.com.android.tools.r8.a.m(j12, 86400000000000L);
        if (m4 != T3) {
            mVar = m.L(m4);
        }
        return T(iVar.S(n4), mVar);
    }

    private k T(i iVar, m mVar) {
        return (this.f3979a == iVar && this.f3980b == mVar) ? this : new k(iVar, mVar);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 5, this);
    }

    public final int F() {
        return this.f3980b.I();
    }

    public final int G() {
        return this.f3980b.J();
    }

    public final int H() {
        return this.f3979a.K();
    }

    public final boolean I(k kVar) {
        if (kVar instanceof k) {
            return D(kVar) > 0;
        }
        int i4 = (this.f3979a.s() > kVar.f3979a.s() ? 1 : (this.f3979a.s() == kVar.f3979a.s() ? 0 : -1));
        return i4 > 0 || (i4 == 0 && this.f3980b.T() > kVar.f3980b.T());
    }

    public final boolean J(k kVar) {
        if (kVar instanceof k) {
            return D(kVar) < 0;
        }
        int i4 = (this.f3979a.s() > kVar.f3979a.s() ? 1 : (this.f3979a.s() == kVar.f3979a.s() ? 0 : -1));
        return i4 < 0 || (i4 == 0 && this.f3980b.T() < kVar.f3980b.T());
    }

    @Override // j$.time.temporal.m
    /* renamed from: N */
    public final k e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            int i4 = j.f3976a[((j$.time.temporal.b) uVar).ordinal()];
            m mVar = this.f3980b;
            i iVar = this.f3979a;
            switch (i4) {
                case 1:
                    return P(this.f3979a, 0L, 0L, 0L, j4);
                case 2:
                    k T3 = T(iVar.S(j4 / 86400000000L), mVar);
                    return T3.P(T3.f3979a, 0L, 0L, 0L, (j4 % 86400000000L) * 1000);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    k T4 = T(iVar.S(j4 / 86400000), mVar);
                    return T4.P(T4.f3979a, 0L, 0L, 0L, (j4 % 86400000) * 1000000);
                case 4:
                    return O(j4);
                case 5:
                    return P(this.f3979a, 0L, j4, 0L, 0L);
                case 6:
                    return P(this.f3979a, j4, 0L, 0L, 0L);
                case 7:
                    k T5 = T(iVar.S(j4 / 256), mVar);
                    return T5.P(T5.f3979a, (j4 % 256) * 12, 0L, 0L, 0L);
                default:
                    return T(iVar.e(j4, uVar), mVar);
            }
        }
        return (k) uVar.j(this, j4);
    }

    public final k O(long j4) {
        return P(this.f3979a, 0L, 0L, j4, 0L);
    }

    public final i Q() {
        return this.f3979a;
    }

    @Override // j$.time.temporal.m
    /* renamed from: R */
    public final k d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            boolean E4 = ((j$.time.temporal.a) rVar).E();
            m mVar = this.f3980b;
            i iVar = this.f3979a;
            return E4 ? T(iVar, mVar.d(j4, rVar)) : T(iVar.d(j4, rVar), mVar);
        }
        return (k) rVar.r(this, j4);
    }

    public final k S(i iVar) {
        return T(iVar, this.f3980b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void U(DataOutput dataOutput) {
        this.f3979a.a0(dataOutput);
        this.f3980b.X(dataOutput);
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final j$.time.chrono.n a() {
        return ((i) c()).a();
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final m b() {
        return this.f3980b;
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final InterfaceC0484b c() {
        return this.f3979a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof k) {
            k kVar = (k) obj;
            return this.f3979a.equals(kVar.f3979a) && this.f3980b.equals(kVar.f3980b);
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        if (!(rVar instanceof j$.time.temporal.a)) {
            return rVar != null && rVar.m(this);
        }
        j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
        return aVar.v() || aVar.E();
    }

    public final int hashCode() {
        return this.f3979a.hashCode() ^ this.f3980b.hashCode();
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() ? this.f3980b.j(rVar) : this.f3979a.j(rVar) : j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        return T(iVar, this.f3980b);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            if (((j$.time.temporal.a) rVar).E()) {
                m mVar = this.f3980b;
                mVar.getClass();
                return j$.time.temporal.n.d(mVar, rVar);
            }
            return this.f3979a.m(rVar);
        }
        return rVar.u(this);
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final InterfaceC0493k o(B b4) {
        return E.F(this, b4, null);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() ? this.f3980b.r(rVar) : this.f3979a.r(rVar) : rVar.l(this);
    }

    public final String toString() {
        String iVar = this.f3979a.toString();
        String mVar = this.f3980b.toString();
        return iVar + "T" + mVar;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.f() ? this.f3979a : AbstractC0491i.k(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(((i) c()).s(), j$.time.temporal.a.EPOCH_DAY).d(b().T(), j$.time.temporal.a.NANO_OF_DAY);
    }

    @Override // java.lang.Comparable
    /* renamed from: w */
    public final int compareTo(InterfaceC0487e interfaceC0487e) {
        return interfaceC0487e instanceof k ? D((k) interfaceC0487e) : AbstractC0491i.c(this, interfaceC0487e);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
