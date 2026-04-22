package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class t implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {
    private static final long serialVersionUID = 7264499704384272492L;

    /* renamed from: a  reason: collision with root package name */
    private final m f3999a;

    /* renamed from: b  reason: collision with root package name */
    private final B f4000b;

    static {
        m mVar = m.f3983e;
        B b4 = B.f3839g;
        mVar.getClass();
        D(mVar, b4);
        m mVar2 = m.f;
        B b5 = B.f;
        mVar2.getClass();
        D(mVar2, b5);
    }

    private t(m mVar, B b4) {
        this.f3999a = (m) Objects.requireNonNull(mVar, "time");
        this.f4000b = (B) Objects.requireNonNull(b4, "offset");
    }

    public static t D(m mVar, B b4) {
        return new t(mVar, b4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static t F(ObjectInput objectInput) {
        return new t(m.S(objectInput), B.O(objectInput));
    }

    private t G(m mVar, B b4) {
        return (this.f3999a == mVar && this.f4000b.equals(b4)) ? this : new t(mVar, b4);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 9, this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final t e(long j4, j$.time.temporal.u uVar) {
        return uVar instanceof j$.time.temporal.b ? G(this.f3999a.e(j4, uVar), this.f4000b) : (t) uVar.j(this, j4);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        int compare;
        t tVar = (t) obj;
        B b4 = tVar.f4000b;
        B b5 = this.f4000b;
        boolean equals = b5.equals(b4);
        m mVar = tVar.f3999a;
        m mVar2 = this.f3999a;
        return (equals || (compare = Long.compare(mVar2.T() - (((long) b5.J()) * 1000000000), mVar.T() - (((long) tVar.f4000b.J()) * 1000000000))) == 0) ? mVar2.compareTo(mVar) : compare;
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = j$.time.temporal.a.OFFSET_SECONDS;
            m mVar = this.f3999a;
            return rVar == aVar ? G(mVar, B.M(((j$.time.temporal.a) rVar).z(j4))) : G(mVar.d(j4, rVar), this.f4000b);
        }
        return (t) rVar.r(this, j4);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof t) {
            t tVar = (t) obj;
            return this.f3999a.equals(tVar.f3999a) && this.f4000b.equals(tVar.f4000b);
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() || rVar == j$.time.temporal.a.OFFSET_SECONDS : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        return this.f3999a.hashCode() ^ this.f4000b.hashCode();
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        if (iVar instanceof m) {
            return G((m) iVar, this.f4000b);
        }
        if (iVar instanceof B) {
            return G(this.f3999a, (B) iVar);
        }
        boolean z4 = iVar instanceof t;
        Object obj = iVar;
        if (!z4) {
            obj = AbstractC0491i.a(iVar, this);
        }
        return (t) obj;
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            if (rVar == j$.time.temporal.a.OFFSET_SECONDS) {
                return ((j$.time.temporal.a) rVar).j();
            }
            m mVar = this.f3999a;
            mVar.getClass();
            return j$.time.temporal.n.d(mVar, rVar);
        }
        return rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.OFFSET_SECONDS ? this.f4000b.J() : this.f3999a.r(rVar) : rVar.l(this);
    }

    public final String toString() {
        String mVar = this.f3999a.toString();
        String b4 = this.f4000b.toString();
        return mVar + b4;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.h() || tVar == j$.time.temporal.n.j()) {
            return this.f4000b;
        }
        if (((tVar == j$.time.temporal.n.k()) || (tVar == j$.time.temporal.n.e())) || tVar == j$.time.temporal.n.f()) {
            return null;
        }
        return tVar == j$.time.temporal.n.g() ? this.f3999a : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.NANOS : tVar.a(this);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(this.f3999a.T(), j$.time.temporal.a.NANO_OF_DAY).d(this.f4000b.J(), j$.time.temporal.a.OFFSET_SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeExternal(ObjectOutput objectOutput) {
        this.f3999a.X(objectOutput);
        this.f4000b.P(objectOutput);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
