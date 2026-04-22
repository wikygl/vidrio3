package j$.time.chrono;

import j$.time.AbstractC0495d;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class s extends AbstractC0486d {
    private static final long serialVersionUID = -5207853542612002020L;

    /* renamed from: a  reason: collision with root package name */
    private final transient q f3901a;

    /* renamed from: b  reason: collision with root package name */
    private final transient int f3902b;

    /* renamed from: c  reason: collision with root package name */
    private final transient int f3903c;

    /* renamed from: d  reason: collision with root package name */
    private final transient int f3904d;

    private s(q qVar, int i4, int i5, int i6) {
        qVar.z(i4, i5, i6);
        this.f3901a = qVar;
        this.f3902b = i4;
        this.f3903c = i5;
        this.f3904d = i6;
    }

    private s(q qVar, long j4) {
        int[] D4 = qVar.D((int) j4);
        this.f3901a = qVar;
        this.f3902b = D4[0];
        this.f3903c = D4[1];
        this.f3904d = D4[2];
    }

    private int K() {
        return this.f3901a.v(this.f3902b, this.f3903c) + this.f3904d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static s L(q qVar, int i4, int i5, int i6) {
        return new s(qVar, i4, i5, i6);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static s M(q qVar, long j4) {
        return new s(qVar, j4);
    }

    private s P(int i4, int i5, int i6) {
        q qVar = this.f3901a;
        int E4 = qVar.E(i4, i5);
        if (i6 > E4) {
            i6 = E4;
        }
        return new s(qVar, i4, i5, i6);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 6, this);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final o E() {
        return t.AH;
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b F(long j4, j$.time.temporal.u uVar) {
        return (s) super.z(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b I(long j4) {
        if (j4 == 0) {
            return this;
        }
        long j5 = this.f3902b + ((int) j4);
        int i4 = (int) j5;
        if (j5 == i4) {
            return P(i4, this.f3903c, this.f3904d);
        }
        throw new ArithmeticException();
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b J(j$.time.temporal.p pVar) {
        return (s) super.l(pVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.chrono.AbstractC0486d
    /* renamed from: N */
    public final s G(long j4) {
        return new s(this.f3901a, s() + j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.chrono.AbstractC0486d
    /* renamed from: O */
    public final s H(long j4) {
        if (j4 == 0) {
            return this;
        }
        long j5 = (this.f3902b * 12) + (this.f3903c - 1) + j4;
        return P(this.f3901a.r(j$.com.android.tools.r8.a.n(j5, 12L)), ((int) j$.com.android.tools.r8.a.m(j5, 12L)) + 1, this.f3904d);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    /* renamed from: Q */
    public final s d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            q qVar = this.f3901a;
            qVar.H(aVar).b(j4, aVar);
            int i4 = (int) j4;
            int i5 = r.f3900a[aVar.ordinal()];
            int i6 = this.f3904d;
            int i7 = this.f3903c;
            int i8 = this.f3902b;
            switch (i5) {
                case 1:
                    return P(i8, i7, i4);
                case 2:
                    return G(Math.min(i4, qVar.F(i8)) - K());
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return G((j4 - r(j$.time.temporal.a.ALIGNED_WEEK_OF_MONTH)) * 7);
                case 4:
                    return G(j4 - (((int) j$.com.android.tools.r8.a.m(s() + 3, 7)) + 1));
                case 5:
                    return G(j4 - r(j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case 6:
                    return G(j4 - r(j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case 7:
                    return new s(qVar, j4);
                case 8:
                    return G((j4 - r(j$.time.temporal.a.ALIGNED_WEEK_OF_YEAR)) * 7);
                case 9:
                    return P(i8, i4, i6);
                case 10:
                    return H(j4 - (((i8 * 12) + i7) - 1));
                case 11:
                    if (i8 < 1) {
                        i4 = 1 - i4;
                    }
                    return P(i4, i7, i6);
                case 12:
                    return P(i4, i7, i6);
                case 13:
                    return P(1 - i8, i7, i6);
                default:
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            }
        }
        return (s) super.d(j4, rVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final n a() {
        return this.f3901a;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b, j$.time.temporal.m
    public final InterfaceC0484b e(long j4, j$.time.temporal.u uVar) {
        return (s) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m e(long j4, j$.time.temporal.u uVar) {
        return (s) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof s) {
            s sVar = (s) obj;
            return this.f3902b == sVar.f3902b && this.f3903c == sVar.f3903c && this.f3904d == sVar.f3904d && this.f3901a.equals(sVar.f3901a);
        }
        return false;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final int hashCode() {
        int hashCode = this.f3901a.i().hashCode();
        int i4 = this.f3902b;
        return (hashCode ^ (i4 & (-2048))) ^ (((i4 << 11) + (this.f3903c << 6)) + this.f3904d);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m l(j$.time.i iVar) {
        return (s) super.l(iVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        int E4;
        long j4;
        if (rVar instanceof j$.time.temporal.a) {
            if (AbstractC0491i.h(this, rVar)) {
                j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
                int i4 = r.f3900a[aVar.ordinal()];
                int i5 = this.f3902b;
                q qVar = this.f3901a;
                if (i4 == 1) {
                    E4 = qVar.E(i5, this.f3903c);
                } else if (i4 != 2) {
                    if (i4 != 3) {
                        return qVar.H(aVar);
                    }
                    j4 = 5;
                    return j$.time.temporal.w.j(1L, j4);
                } else {
                    E4 = qVar.F(i5);
                }
                j4 = E4;
                return j$.time.temporal.w.j(1L, j4);
            }
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = r.f3900a[((j$.time.temporal.a) rVar).ordinal()];
            int i5 = this.f3903c;
            int i6 = this.f3904d;
            int i7 = this.f3902b;
            switch (i4) {
                case 1:
                    return i6;
                case 2:
                    return K();
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return ((i6 - 1) / 7) + 1;
                case 4:
                    return ((int) j$.com.android.tools.r8.a.m(s() + 3, 7)) + 1;
                case 5:
                    return ((i6 - 1) % 7) + 1;
                case 6:
                    return ((K() - 1) % 7) + 1;
                case 7:
                    return s();
                case 8:
                    return ((K() - 1) / 7) + 1;
                case 9:
                    return i5;
                case 10:
                    return ((i7 * 12) + i5) - 1;
                case 11:
                    return i7;
                case 12:
                    return i7;
                case 13:
                    return i7 <= 1 ? 0 : 1;
                default:
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            }
        }
        return rVar.l(this);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final long s() {
        return this.f3901a.z(this.f3902b, this.f3903c, this.f3904d);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final InterfaceC0487e t(j$.time.m mVar) {
        return C0489g.F(this, mVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeExternal(ObjectOutput objectOutput) {
        objectOutput.writeObject(this.f3901a);
        objectOutput.writeInt(j$.time.temporal.n.a(this, j$.time.temporal.a.YEAR));
        objectOutput.writeByte(j$.time.temporal.n.a(this, j$.time.temporal.a.MONTH_OF_YEAR));
        objectOutput.writeByte(j$.time.temporal.n.a(this, j$.time.temporal.a.DAY_OF_MONTH));
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return (s) super.z(j4, uVar);
    }
}
