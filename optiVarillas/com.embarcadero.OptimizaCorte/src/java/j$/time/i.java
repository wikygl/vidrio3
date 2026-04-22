package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.time.chrono.InterfaceC0484b;
import j$.time.chrono.InterfaceC0487e;
import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class i implements j$.time.temporal.m, j$.time.temporal.p, InterfaceC0484b, Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final i f3971d = O(-999999999, 1, 1);

    /* renamed from: e  reason: collision with root package name */
    public static final i f3972e = O(999999999, 12, 31);
    private static final long serialVersionUID = 2942565459149668126L;

    /* renamed from: a  reason: collision with root package name */
    private final int f3973a;

    /* renamed from: b  reason: collision with root package name */
    private final short f3974b;

    /* renamed from: c  reason: collision with root package name */
    private final short f3975c;

    static {
        O(1970, 1, 1);
    }

    private i(int i4, int i5, int i6) {
        this.f3973a = i4;
        this.f3974b = (short) i5;
        this.f3975c = (short) i6;
    }

    private static i E(int i4, int i5, int i6) {
        int i7 = 28;
        if (i6 > 28) {
            if (i5 != 2) {
                i7 = (i5 == 4 || i5 == 6 || i5 == 9 || i5 == 11) ? 30 : 31;
            } else {
                j$.time.chrono.u.f3906d.getClass();
                if (j$.time.chrono.u.m(i4)) {
                    i7 = 29;
                }
            }
            if (i6 > i7) {
                if (i6 == 29) {
                    throw new RuntimeException("Invalid date 'February 29' as '" + i4 + "' is not a leap year");
                }
                String name = o.G(i5).name();
                throw new RuntimeException("Invalid date '" + name + " " + i6 + "'");
            }
        }
        return new i(i4, i5, i6);
    }

    public static i F(j$.time.temporal.o oVar) {
        Objects.requireNonNull(oVar, "temporal");
        i iVar = (i) oVar.u(j$.time.temporal.n.f());
        if (iVar != null) {
            return iVar;
        }
        String name = oVar.getClass().getName();
        throw new RuntimeException("Unable to obtain LocalDate from TemporalAccessor: " + oVar + " of type " + name);
    }

    private int G(j$.time.temporal.r rVar) {
        int i4;
        int i5 = h.f3969a[((j$.time.temporal.a) rVar).ordinal()];
        short s4 = this.f3975c;
        int i6 = this.f3973a;
        switch (i5) {
            case 1:
                return s4;
            case 2:
                return I();
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                i4 = (s4 - 1) / 7;
                break;
            case 4:
                return i6 >= 1 ? i6 : 1 - i6;
            case 5:
                return H().getValue();
            case 6:
                i4 = (s4 - 1) % 7;
                break;
            case 7:
                return ((I() - 1) % 7) + 1;
            case 8:
                throw new RuntimeException("Invalid field 'EpochDay' for get() method, use getLong() instead");
            case 9:
                return ((I() - 1) / 7) + 1;
            case 10:
                return this.f3974b;
            case 11:
                throw new RuntimeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
            case 12:
                return i6;
            case 13:
                return i6 >= 1 ? 1 : 0;
            default:
                throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return i4 + 1;
    }

    public static i O(int i4, int i5, int i6) {
        j$.time.temporal.a.YEAR.D(i4);
        j$.time.temporal.a.MONTH_OF_YEAR.D(i5);
        j$.time.temporal.a.DAY_OF_MONTH.D(i6);
        return E(i4, i5, i6);
    }

    public static i P(int i4, o oVar, int i5) {
        j$.time.temporal.a.YEAR.D(i4);
        Objects.requireNonNull(oVar, "month");
        j$.time.temporal.a.DAY_OF_MONTH.D(i5);
        return E(i4, oVar.getValue(), i5);
    }

    public static i Q(long j4) {
        long j5;
        j$.time.temporal.a.EPOCH_DAY.D(j4);
        long j6 = 719468 + j4;
        if (j6 < 0) {
            long j7 = ((j4 + 719469) / 146097) - 1;
            j5 = j7 * 400;
            j6 += (-j7) * 146097;
        } else {
            j5 = 0;
        }
        long j8 = ((j6 * 400) + 591) / 146097;
        long j9 = j6 - ((j8 / 400) + (((j8 / 4) + (j8 * 365)) - (j8 / 100)));
        if (j9 < 0) {
            j8--;
            j9 = j6 - ((j8 / 400) + (((j8 / 4) + (365 * j8)) - (j8 / 100)));
        }
        int i4 = (int) j9;
        int i5 = ((i4 * 5) + 2) / 153;
        return new i(j$.time.temporal.a.YEAR.z(j8 + j5 + (i5 / 10)), ((i5 + 2) % 12) + 1, (i4 - (((i5 * 306) + 5) / 10)) + 1);
    }

    private static i V(int i4, int i5, int i6) {
        int i7;
        if (i5 != 2) {
            if (i5 == 4 || i5 == 6 || i5 == 9 || i5 == 11) {
                i7 = 30;
            }
            return new i(i4, i5, i6);
        }
        j$.time.chrono.u.f3906d.getClass();
        i7 = j$.time.chrono.u.m((long) i4) ? 29 : 28;
        i6 = Math.min(i6, i7);
        return new i(i4, i5, i6);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 3, this);
    }

    @Override // java.lang.Comparable
    /* renamed from: A */
    public final int compareTo(InterfaceC0484b interfaceC0484b) {
        return interfaceC0484b instanceof i ? D((i) interfaceC0484b) : AbstractC0491i.b(this, interfaceC0484b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int D(i iVar) {
        int i4 = this.f3973a - iVar.f3973a;
        if (i4 == 0) {
            int i5 = this.f3974b - iVar.f3974b;
            return i5 == 0 ? this.f3975c - iVar.f3975c : i5;
        }
        return i4;
    }

    public final EnumC0496e H() {
        return EnumC0496e.D(((int) j$.com.android.tools.r8.a.m(s() + 3, 7)) + 1);
    }

    public final int I() {
        return (o.G(this.f3974b).D(M()) + this.f3975c) - 1;
    }

    public final int J() {
        return this.f3974b;
    }

    public final int K() {
        return this.f3973a;
    }

    public final boolean L(InterfaceC0484b interfaceC0484b) {
        return interfaceC0484b instanceof i ? D((i) interfaceC0484b) < 0 : s() < interfaceC0484b.s();
    }

    public final boolean M() {
        j$.time.chrono.u.f3906d.getClass();
        return j$.time.chrono.u.m(this.f3973a);
    }

    public final int N() {
        short s4 = this.f3974b;
        return s4 != 2 ? (s4 == 4 || s4 == 6 || s4 == 9 || s4 == 11) ? 30 : 31 : M() ? 29 : 28;
    }

    @Override // j$.time.temporal.m
    /* renamed from: R */
    public final i e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            switch (h.f3970b[((j$.time.temporal.b) uVar).ordinal()]) {
                case 1:
                    return S(j4);
                case 2:
                    return S(j$.com.android.tools.r8.a.o(j4, 7));
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return T(j4);
                case 4:
                    return U(j4);
                case 5:
                    return U(j$.com.android.tools.r8.a.o(j4, 10));
                case 6:
                    return U(j$.com.android.tools.r8.a.o(j4, 100));
                case 7:
                    return U(j$.com.android.tools.r8.a.o(j4, 1000));
                case 8:
                    j$.time.temporal.a aVar = j$.time.temporal.a.ERA;
                    return d(j$.com.android.tools.r8.a.i(r(aVar), j4), aVar);
                default:
                    throw new RuntimeException("Unsupported unit: " + uVar);
            }
        }
        return (i) uVar.j(this, j4);
    }

    public final i S(long j4) {
        if (j4 == 0) {
            return this;
        }
        long j5 = this.f3975c + j4;
        if (j5 > 0) {
            short s4 = this.f3974b;
            int i4 = this.f3973a;
            if (j5 <= 28) {
                return new i(i4, s4, (int) j5);
            }
            if (j5 <= 59) {
                long N3 = N();
                if (j5 <= N3) {
                    return new i(i4, s4, (int) j5);
                }
                if (s4 < 12) {
                    return new i(i4, s4 + 1, (int) (j5 - N3));
                }
                int i5 = i4 + 1;
                j$.time.temporal.a.YEAR.D(i5);
                return new i(i5, 1, (int) (j5 - N3));
            }
        }
        return Q(j$.com.android.tools.r8.a.i(s(), j4));
    }

    public final i T(long j4) {
        if (j4 == 0) {
            return this;
        }
        long j5 = (this.f3973a * 12) + (this.f3974b - 1) + j4;
        long j6 = 12;
        return V(j$.time.temporal.a.YEAR.z(j$.com.android.tools.r8.a.n(j5, j6)), ((int) j$.com.android.tools.r8.a.m(j5, j6)) + 1, this.f3975c);
    }

    public final i U(long j4) {
        return j4 == 0 ? this : V(j$.time.temporal.a.YEAR.z(this.f3973a + j4), this.f3974b, this.f3975c);
    }

    @Override // j$.time.temporal.m
    /* renamed from: W */
    public final i d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            aVar.D(j4);
            int i4 = h.f3969a[aVar.ordinal()];
            short s4 = this.f3975c;
            short s5 = this.f3974b;
            int i5 = this.f3973a;
            switch (i4) {
                case 1:
                    int i6 = (int) j4;
                    return s4 == i6 ? this : O(i5, s5, i6);
                case 2:
                    return Y((int) j4);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return S(j$.com.android.tools.r8.a.o(j4 - r(j$.time.temporal.a.ALIGNED_WEEK_OF_MONTH), 7));
                case 4:
                    if (i5 < 1) {
                        j4 = 1 - j4;
                    }
                    return Z((int) j4);
                case 5:
                    return S(j4 - H().getValue());
                case 6:
                    return S(j4 - r(j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case 7:
                    return S(j4 - r(j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case 8:
                    return Q(j4);
                case 9:
                    return S(j$.com.android.tools.r8.a.o(j4 - r(j$.time.temporal.a.ALIGNED_WEEK_OF_YEAR), 7));
                case 10:
                    int i7 = (int) j4;
                    if (s5 == i7) {
                        return this;
                    }
                    j$.time.temporal.a.MONTH_OF_YEAR.D(i7);
                    return V(i5, i7, s4);
                case 11:
                    return T(j4 - (((i5 * 12) + s5) - 1));
                case 12:
                    return Z((int) j4);
                case 13:
                    return r(j$.time.temporal.a.ERA) == j4 ? this : Z(1 - i5);
                default:
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            }
        }
        return (i) rVar.r(this, j4);
    }

    @Override // j$.time.temporal.m
    /* renamed from: X */
    public final i l(j$.time.temporal.p pVar) {
        return pVar instanceof i ? (i) pVar : (i) pVar.v(this);
    }

    public final i Y(int i4) {
        if (I() == i4) {
            return this;
        }
        j$.time.temporal.a aVar = j$.time.temporal.a.YEAR;
        int i5 = this.f3973a;
        long j4 = i5;
        aVar.D(j4);
        j$.time.temporal.a.DAY_OF_YEAR.D(i4);
        j$.time.chrono.u.f3906d.getClass();
        boolean m4 = j$.time.chrono.u.m(j4);
        if (i4 == 366 && !m4) {
            throw new RuntimeException("Invalid date 'DayOfYear 366' as '" + i5 + "' is not a leap year");
        }
        o G4 = o.G(((i4 - 1) / 31) + 1);
        if (i4 > (G4.E(m4) + G4.D(m4)) - 1) {
            G4 = G4.H();
        }
        return new i(i5, G4.getValue(), (i4 - G4.D(m4)) + 1);
    }

    public final i Z(int i4) {
        if (this.f3973a == i4) {
            return this;
        }
        j$.time.temporal.a.YEAR.D(i4);
        return V(i4, this.f3974b, this.f3975c);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final j$.time.chrono.n a() {
        return j$.time.chrono.u.f3906d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a0(DataOutput dataOutput) {
        dataOutput.writeInt(this.f3973a);
        dataOutput.writeByte(this.f3974b);
        dataOutput.writeByte(this.f3975c);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof i) && D((i) obj) == 0;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return AbstractC0491i.h(this, rVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final int hashCode() {
        int i4 = this.f3973a;
        return (((i4 << 11) + (this.f3974b << 6)) + this.f3975c) ^ (i4 & (-2048));
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? G(rVar) : j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            if (aVar.v()) {
                int i4 = h.f3969a[aVar.ordinal()];
                if (i4 != 1) {
                    if (i4 == 2) {
                        return j$.time.temporal.w.j(1L, M() ? 366 : 365);
                    } else if (i4 == 3) {
                        return j$.time.temporal.w.j(1L, (o.G(this.f3974b) != o.FEBRUARY || M()) ? 5L : 4L);
                    } else if (i4 != 4) {
                        return ((j$.time.temporal.a) rVar).j();
                    } else {
                        return j$.time.temporal.w.j(1L, this.f3973a <= 0 ? 1000000000L : 999999999L);
                    }
                }
                return j$.time.temporal.w.j(1L, N());
            }
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.EPOCH_DAY ? s() : rVar == j$.time.temporal.a.PROLEPTIC_MONTH ? ((this.f3973a * 12) + this.f3974b) - 1 : G(rVar) : rVar.l(this);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final long s() {
        long j4 = this.f3973a;
        long j5 = this.f3974b;
        long j6 = 365 * j4;
        long j7 = (((367 * j5) - 362) / 12) + (j4 >= 0 ? ((j4 + 399) / 400) + (((3 + j4) / 4) - ((99 + j4) / 100)) + j6 : j6 - ((j4 / (-400)) + ((j4 / (-4)) - (j4 / (-100))))) + (this.f3975c - 1);
        if (j5 > 2) {
            j7 = !M() ? j7 - 2 : j7 - 1;
        }
        return j7 - 719528;
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final InterfaceC0487e t(m mVar) {
        return k.L(this, mVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final String toString() {
        int i4;
        int i5 = this.f3973a;
        int abs = Math.abs(i5);
        StringBuilder sb = new StringBuilder(10);
        if (abs < 1000) {
            if (i5 < 0) {
                sb.append(i5 - 10000);
                i4 = 1;
            } else {
                sb.append(i5 + 10000);
                i4 = 0;
            }
            sb.deleteCharAt(i4);
        } else {
            if (i5 > 9999) {
                sb.append('+');
            }
            sb.append(i5);
        }
        short s4 = this.f3974b;
        sb.append(s4 < 10 ? "-0" : "-");
        sb.append((int) s4);
        short s5 = this.f3975c;
        sb.append(s5 < 10 ? "-0" : "-");
        sb.append((int) s5);
        return sb.toString();
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.f() ? this : AbstractC0491i.j(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return AbstractC0491i.a(this, mVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
