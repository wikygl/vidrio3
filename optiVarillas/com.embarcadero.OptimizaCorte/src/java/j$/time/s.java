package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class s implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {
    private static final long serialVersionUID = 2287754244819255394L;

    /* renamed from: a  reason: collision with root package name */
    private final k f3997a;

    /* renamed from: b  reason: collision with root package name */
    private final B f3998b;

    static {
        k kVar = k.f3977c;
        B b4 = B.f3839g;
        kVar.getClass();
        D(kVar, b4);
        k kVar2 = k.f3978d;
        B b5 = B.f;
        kVar2.getClass();
        D(kVar2, b5);
    }

    private s(k kVar, B b4) {
        this.f3997a = (k) Objects.requireNonNull(kVar, "dateTime");
        this.f3998b = (B) Objects.requireNonNull(b4, "offset");
    }

    public static s D(k kVar, B b4) {
        return new s(kVar, b4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static s F(ObjectInput objectInput) {
        k kVar = k.f3977c;
        i iVar = i.f3971d;
        return new s(k.L(i.O(objectInput.readInt(), objectInput.readByte(), objectInput.readByte()), m.S(objectInput)), B.O(objectInput));
    }

    private s H(k kVar, B b4) {
        return (this.f3997a == kVar && this.f3998b.equals(b4)) ? this : new s(kVar, b4);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 10, this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final s e(long j4, j$.time.temporal.u uVar) {
        return uVar instanceof j$.time.temporal.b ? H(this.f3997a.e(j4, uVar), this.f3998b) : (s) uVar.j(this, j4);
    }

    public final k G() {
        return this.f3997a;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        int compare;
        s sVar = (s) obj;
        B b4 = sVar.f3998b;
        B b5 = this.f3998b;
        boolean equals = b5.equals(b4);
        k kVar = sVar.f3997a;
        k kVar2 = this.f3997a;
        if (equals) {
            compare = kVar2.compareTo(kVar);
        } else {
            kVar2.getClass();
            long n4 = AbstractC0491i.n(kVar2, b5);
            kVar.getClass();
            compare = Long.compare(n4, AbstractC0491i.n(kVar, sVar.f3998b));
            if (compare == 0) {
                compare = kVar2.b().I() - kVar.b().I();
            }
        }
        return compare == 0 ? kVar2.compareTo(kVar) : compare;
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            int i4 = r.f3996a[aVar.ordinal()];
            B b4 = this.f3998b;
            k kVar = this.f3997a;
            if (i4 != 1) {
                return i4 != 2 ? H(kVar.d(j4, rVar), b4) : H(kVar, B.M(aVar.z(j4)));
            }
            g H4 = g.H(j4, kVar.F());
            Objects.requireNonNull(H4, "instant");
            Objects.requireNonNull(b4, "zone");
            B d4 = b4.D().d(H4);
            return new s(k.M(H4.E(), H4.F(), d4), d4);
        }
        return (s) rVar.r(this, j4);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof s) {
            s sVar = (s) obj;
            return this.f3997a.equals(sVar.f3997a) && this.f3998b.equals(sVar.f3998b);
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return (rVar instanceof j$.time.temporal.a) || (rVar != null && rVar.m(this));
    }

    public final int hashCode() {
        return this.f3997a.hashCode() ^ this.f3998b.hashCode();
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = r.f3996a[((j$.time.temporal.a) rVar).ordinal()];
            if (i4 != 1) {
                return i4 != 2 ? this.f3997a.j(rVar) : this.f3998b.J();
            }
            throw new RuntimeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        }
        return j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        return H(this.f3997a.S(iVar), this.f3998b);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? (rVar == j$.time.temporal.a.INSTANT_SECONDS || rVar == j$.time.temporal.a.OFFSET_SECONDS) ? ((j$.time.temporal.a) rVar).j() : this.f3997a.m(rVar) : rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = r.f3996a[((j$.time.temporal.a) rVar).ordinal()];
            B b4 = this.f3998b;
            k kVar = this.f3997a;
            if (i4 != 1) {
                return i4 != 2 ? kVar.r(rVar) : b4.J();
            }
            kVar.getClass();
            return AbstractC0491i.n(kVar, b4);
        }
        return rVar.l(this);
    }

    public final String toString() {
        String kVar = this.f3997a.toString();
        String b4 = this.f3998b.toString();
        return kVar + b4;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.h() || tVar == j$.time.temporal.n.j()) {
            return this.f3998b;
        }
        if (tVar == j$.time.temporal.n.k()) {
            return null;
        }
        j$.time.temporal.t f = j$.time.temporal.n.f();
        k kVar = this.f3997a;
        return tVar == f ? kVar.Q() : tVar == j$.time.temporal.n.g() ? kVar.b() : tVar == j$.time.temporal.n.e() ? j$.time.chrono.u.f3906d : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.NANOS : tVar.a(this);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        j$.time.temporal.a aVar = j$.time.temporal.a.EPOCH_DAY;
        k kVar = this.f3997a;
        return mVar.d(kVar.Q().s(), aVar).d(kVar.b().T(), j$.time.temporal.a.NANO_OF_DAY).d(this.f3998b.J(), j$.time.temporal.a.OFFSET_SECONDS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeExternal(ObjectOutput objectOutput) {
        this.f3997a.U(objectOutput);
        this.f3998b.P(objectOutput);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
