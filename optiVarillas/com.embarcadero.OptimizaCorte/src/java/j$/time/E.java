package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.time.chrono.InterfaceC0484b;
import j$.time.chrono.InterfaceC0487e;
import j$.time.chrono.InterfaceC0493k;
import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class E implements j$.time.temporal.m, InterfaceC0493k, Serializable {
    private static final long serialVersionUID = -6260982410461394882L;

    /* renamed from: a  reason: collision with root package name */
    private final k f3849a;

    /* renamed from: b  reason: collision with root package name */
    private final B f3850b;

    /* renamed from: c  reason: collision with root package name */
    private final A f3851c;

    private E(k kVar, A a4, B b4) {
        this.f3849a = kVar;
        this.f3850b = b4;
        this.f3851c = a4;
    }

    private static E D(long j4, int i4, A a4) {
        B d4 = a4.D().d(g.H(j4, i4));
        return new E(k.M(j4, i4, d4), a4, d4);
    }

    public static E E(g gVar, A a4) {
        Objects.requireNonNull(gVar, "instant");
        Objects.requireNonNull(a4, "zone");
        return D(gVar.E(), gVar.F(), a4);
    }

    public static E F(k kVar, A a4, B b4) {
        Object requireNonNull;
        Objects.requireNonNull(kVar, "localDateTime");
        Objects.requireNonNull(a4, "zone");
        if (a4 instanceof B) {
            return new E(kVar, a4, (B) a4);
        }
        j$.time.zone.f D4 = a4.D();
        List g4 = D4.g(kVar);
        if (g4.size() != 1) {
            if (g4.size() == 0) {
                j$.time.zone.b f = D4.f(kVar);
                kVar = kVar.O(f.m().m());
                b4 = f.r();
            } else if (b4 == null || !g4.contains(b4)) {
                requireNonNull = Objects.requireNonNull((B) g4.get(0), "offset");
            }
            return new E(kVar, a4, b4);
        }
        requireNonNull = g4.get(0);
        b4 = (B) requireNonNull;
        return new E(kVar, a4, b4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static E H(ObjectInput objectInput) {
        k kVar = k.f3977c;
        i iVar = i.f3971d;
        k L3 = k.L(i.O(objectInput.readInt(), objectInput.readByte(), objectInput.readByte()), m.S(objectInput));
        B O3 = B.O(objectInput);
        A a4 = (A) v.a(objectInput);
        Objects.requireNonNull(L3, "localDateTime");
        Objects.requireNonNull(O3, "offset");
        Objects.requireNonNull(a4, "zone");
        if (!(a4 instanceof B) || O3.equals(a4)) {
            return new E(L3, a4, O3);
        }
        throw new IllegalArgumentException("ZoneId must match ZoneOffset");
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 6, this);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final /* synthetic */ long C() {
        return AbstractC0491i.o(this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: G */
    public final E e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            j$.time.temporal.b bVar = (j$.time.temporal.b) uVar;
            int compareTo = bVar.compareTo(j$.time.temporal.b.DAYS);
            B b4 = this.f3850b;
            A a4 = this.f3851c;
            k kVar = this.f3849a;
            if (compareTo < 0 || bVar == j$.time.temporal.b.FOREVER) {
                k e4 = kVar.e(j4, uVar);
                Objects.requireNonNull(e4, "localDateTime");
                Objects.requireNonNull(b4, "offset");
                Objects.requireNonNull(a4, "zone");
                if (a4.D().g(e4).contains(b4)) {
                    return new E(e4, a4, b4);
                }
                e4.getClass();
                return D(AbstractC0491i.n(e4, b4), e4.F(), a4);
            }
            return F(kVar.e(j4, uVar), a4, b4);
        }
        return (E) uVar.j(this, j4);
    }

    public final k I() {
        return this.f3849a;
    }

    @Override // j$.time.temporal.m
    /* renamed from: J */
    public final E l(i iVar) {
        return F(k.L(iVar, this.f3849a.b()), this.f3851c, this.f3850b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void K(DataOutput dataOutput) {
        this.f3849a.U(dataOutput);
        this.f3850b.P(dataOutput);
        this.f3851c.H(dataOutput);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final j$.time.chrono.n a() {
        return ((i) c()).a();
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final m b() {
        return this.f3849a.b();
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0484b c() {
        return this.f3849a.Q();
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            int i4 = D.f3845a[aVar.ordinal()];
            k kVar = this.f3849a;
            A a4 = this.f3851c;
            if (i4 != 1) {
                B b4 = this.f3850b;
                if (i4 != 2) {
                    return F(kVar.d(j4, rVar), a4, b4);
                }
                B M3 = B.M(aVar.z(j4));
                return (M3.equals(b4) || !a4.D().g(kVar).contains(M3)) ? this : new E(kVar, a4, M3);
            }
            return D(j4, kVar.F(), a4);
        }
        return (E) rVar.r(this, j4);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof E) {
            E e4 = (E) obj;
            return this.f3849a.equals(e4.f3849a) && this.f3850b.equals(e4.f3850b) && this.f3851c.equals(e4.f3851c);
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return (rVar instanceof j$.time.temporal.a) || (rVar != null && rVar.m(this));
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final B g() {
        return this.f3850b;
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0493k h(A a4) {
        Objects.requireNonNull(a4, "zone");
        return this.f3851c.equals(a4) ? this : F(this.f3849a, a4, this.f3850b);
    }

    public final int hashCode() {
        return (this.f3849a.hashCode() ^ this.f3850b.hashCode()) ^ Integer.rotateLeft(this.f3851c.hashCode(), 3);
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = D.f3845a[((j$.time.temporal.a) rVar).ordinal()];
            if (i4 != 1) {
                return i4 != 2 ? this.f3849a.j(rVar) : this.f3850b.J();
            }
            throw new RuntimeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        }
        return AbstractC0491i.e(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? (rVar == j$.time.temporal.a.INSTANT_SECONDS || rVar == j$.time.temporal.a.OFFSET_SECONDS) ? ((j$.time.temporal.a) rVar).j() : this.f3849a.m(rVar) : rVar.u(this);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final A p() {
        return this.f3851c;
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = D.f3845a[((j$.time.temporal.a) rVar).ordinal()];
            return i4 != 1 ? i4 != 2 ? this.f3849a.r(rVar) : this.f3850b.J() : AbstractC0491i.o(this);
        }
        return rVar.l(this);
    }

    public final String toString() {
        String kVar = this.f3849a.toString();
        B b4 = this.f3850b;
        String str = kVar + b4.toString();
        A a4 = this.f3851c;
        if (b4 != a4) {
            return str + "[" + a4.toString() + "]";
        }
        return str;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.f() ? this.f3849a.Q() : AbstractC0491i.l(this, tVar);
    }

    @Override // java.lang.Comparable
    /* renamed from: v */
    public final /* synthetic */ int compareTo(InterfaceC0493k interfaceC0493k) {
        return AbstractC0491i.d(this, interfaceC0493k);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0487e x() {
        return this.f3849a;
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
