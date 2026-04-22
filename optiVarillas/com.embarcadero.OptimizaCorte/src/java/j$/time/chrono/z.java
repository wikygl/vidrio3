package j$.time.chrono;

import j$.time.AbstractC0495d;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class z extends AbstractC0486d {

    /* renamed from: d  reason: collision with root package name */
    static final j$.time.i f3911d = j$.time.i.O(1873, 1, 1);
    private static final long serialVersionUID = -305327627230580483L;

    /* renamed from: a  reason: collision with root package name */
    private final transient j$.time.i f3912a;

    /* renamed from: b  reason: collision with root package name */
    private transient A f3913b;

    /* renamed from: c  reason: collision with root package name */
    private transient int f3914c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public z(j$.time.i iVar) {
        if (iVar.L(f3911d)) {
            throw new RuntimeException("JapaneseDate before Meiji 6 is not supported");
        }
        A i4 = A.i(iVar);
        this.f3913b = i4;
        this.f3914c = (iVar.K() - i4.n().K()) + 1;
        this.f3912a = iVar;
    }

    private z L(j$.time.i iVar) {
        return iVar.equals(this.f3912a) ? this : new z(iVar);
    }

    private z M(A a4, int i4) {
        x.f3909d.getClass();
        if (a4 instanceof A) {
            int K3 = (a4.n().K() + i4) - 1;
            if (i4 != 1 && (K3 < -999999999 || K3 > 999999999 || K3 < a4.n().K() || a4 != A.i(j$.time.i.O(K3, 1, 1)))) {
                throw new RuntimeException("Invalid yearOfEra value");
            }
            return L(this.f3912a.Z(K3));
        }
        throw new ClassCastException("Era must be JapaneseEra");
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 4, this);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final o E() {
        return this.f3913b;
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b F(long j4, j$.time.temporal.u uVar) {
        return (z) super.z(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b G(long j4) {
        return L(this.f3912a.S(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b H(long j4) {
        return L(this.f3912a.T(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b I(long j4) {
        return L(this.f3912a.U(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b J(j$.time.temporal.p pVar) {
        return (z) super.l(pVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    /* renamed from: K */
    public final z d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            if (r(aVar) == j4) {
                return this;
            }
            int[] iArr = y.f3910a;
            int i4 = iArr[aVar.ordinal()];
            j$.time.i iVar = this.f3912a;
            if (i4 == 3 || i4 == 8 || i4 == 9) {
                int a4 = x.f3909d.m(aVar).a(j4, aVar);
                int i5 = iArr[aVar.ordinal()];
                if (i5 == 3) {
                    return M(this.f3913b, a4);
                }
                if (i5 == 8) {
                    return M(A.y(a4), this.f3914c);
                }
                if (i5 == 9) {
                    return L(iVar.Z(a4));
                }
            }
            return L(iVar.d(j4, rVar));
        }
        return (z) super.d(j4, rVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final n a() {
        return x.f3909d;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b, j$.time.temporal.m
    public final InterfaceC0484b e(long j4, j$.time.temporal.u uVar) {
        return (z) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m e(long j4, j$.time.temporal.u uVar) {
        return (z) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof z) {
            return this.f3912a.equals(((z) obj).f3912a);
        }
        return false;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b, j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_MONTH || rVar == j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_YEAR || rVar == j$.time.temporal.a.ALIGNED_WEEK_OF_MONTH || rVar == j$.time.temporal.a.ALIGNED_WEEK_OF_YEAR) {
            return false;
        }
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).v() : rVar != null && rVar.m(this);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final int hashCode() {
        x.f3909d.getClass();
        return this.f3912a.hashCode() ^ (-688086063);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m l(j$.time.i iVar) {
        return (z) super.l(iVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        int N3;
        long j4;
        if (rVar instanceof j$.time.temporal.a) {
            if (f(rVar)) {
                j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
                int i4 = y.f3910a[aVar.ordinal()];
                j$.time.i iVar = this.f3912a;
                if (i4 != 1) {
                    A a4 = this.f3913b;
                    if (i4 != 2) {
                        if (i4 != 3) {
                            return x.f3909d.m(aVar);
                        }
                        int K3 = a4.n().K();
                        A q4 = a4.q();
                        j4 = q4 != null ? (q4.n().K() - K3) + 1 : 999999999 - K3;
                        return j$.time.temporal.w.j(1L, j4);
                    }
                    A q5 = a4.q();
                    N3 = (q5 == null || q5.n().K() != iVar.K()) ? iVar.M() ? 366 : 365 : q5.n().I() - 1;
                    if (this.f3914c == 1) {
                        N3 -= a4.n().I() - 1;
                    }
                } else {
                    N3 = iVar.N();
                }
                j4 = N3;
                return j$.time.temporal.w.j(1L, j4);
            }
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = y.f3910a[((j$.time.temporal.a) rVar).ordinal()];
            int i5 = this.f3914c;
            A a4 = this.f3913b;
            j$.time.i iVar = this.f3912a;
            switch (i4) {
                case 2:
                    return i5 == 1 ? (iVar.I() - a4.n().I()) + 1 : iVar.I();
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return i5;
                case 4:
                case 5:
                case 6:
                case 7:
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
                case 8:
                    return a4.getValue();
                default:
                    return iVar.r(rVar);
            }
        }
        return rVar.l(this);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final long s() {
        return this.f3912a.s();
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final InterfaceC0487e t(j$.time.m mVar) {
        return C0489g.F(this, mVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return (z) super.z(j4, uVar);
    }
}
