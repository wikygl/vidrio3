package j$.time;

import j$.time.chrono.AbstractC0483a;
import j$.time.chrono.AbstractC0491i;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class z implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {
    private static final long serialVersionUID = 4183400860270640070L;

    /* renamed from: a  reason: collision with root package name */
    private final int f4044a;

    /* renamed from: b  reason: collision with root package name */
    private final int f4045b;

    static {
        j$.time.format.o oVar = new j$.time.format.o();
        oVar.l(j$.time.temporal.a.YEAR, 4, 10, j$.time.format.v.EXCEEDS_PAD);
        oVar.e('-');
        oVar.k(j$.time.temporal.a.MONTH_OF_YEAR, 2);
        oVar.v();
    }

    private z(int i4, int i5) {
        this.f4044a = i4;
        this.f4045b = i5;
    }

    private long D() {
        return ((this.f4044a * 12) + this.f4045b) - 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static z H(DataInput dataInput) {
        int readInt = dataInput.readInt();
        byte readByte = dataInput.readByte();
        j$.time.temporal.a.YEAR.D(readInt);
        j$.time.temporal.a.MONTH_OF_YEAR.D(readByte);
        return new z(readInt, readByte);
    }

    private z I(int i4, int i5) {
        return (this.f4044a == i4 && this.f4045b == i5) ? this : new z(i4, i5);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 12, this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final z e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            switch (y.f4043b[((j$.time.temporal.b) uVar).ordinal()]) {
                case 1:
                    return F(j4);
                case 2:
                    return G(j4);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return G(j$.com.android.tools.r8.a.o(j4, 10));
                case 4:
                    return G(j$.com.android.tools.r8.a.o(j4, 100));
                case 5:
                    return G(j$.com.android.tools.r8.a.o(j4, 1000));
                case 6:
                    j$.time.temporal.a aVar = j$.time.temporal.a.ERA;
                    return d(j$.com.android.tools.r8.a.i(r(aVar), j4), aVar);
                default:
                    throw new RuntimeException("Unsupported unit: " + uVar);
            }
        }
        return (z) uVar.j(this, j4);
    }

    public final z F(long j4) {
        if (j4 == 0) {
            return this;
        }
        long j5 = (this.f4044a * 12) + (this.f4045b - 1) + j4;
        long j6 = 12;
        return I(j$.time.temporal.a.YEAR.z(j$.com.android.tools.r8.a.n(j5, j6)), ((int) j$.com.android.tools.r8.a.m(j5, j6)) + 1);
    }

    public final z G(long j4) {
        return j4 == 0 ? this : I(j$.time.temporal.a.YEAR.z(this.f4044a + j4), this.f4045b);
    }

    @Override // j$.time.temporal.m
    /* renamed from: J */
    public final z d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            aVar.D(j4);
            int i4 = y.f4042a[aVar.ordinal()];
            int i5 = this.f4044a;
            if (i4 == 1) {
                int i6 = (int) j4;
                j$.time.temporal.a.MONTH_OF_YEAR.D(i6);
                return I(i5, i6);
            } else if (i4 != 2) {
                int i7 = this.f4045b;
                if (i4 == 3) {
                    if (i5 < 1) {
                        j4 = 1 - j4;
                    }
                    int i8 = (int) j4;
                    j$.time.temporal.a.YEAR.D(i8);
                    return I(i8, i7);
                } else if (i4 == 4) {
                    int i9 = (int) j4;
                    j$.time.temporal.a.YEAR.D(i9);
                    return I(i9, i7);
                } else if (i4 == 5) {
                    if (r(j$.time.temporal.a.ERA) == j4) {
                        return this;
                    }
                    int i10 = 1 - i5;
                    j$.time.temporal.a.YEAR.D(i10);
                    return I(i10, i7);
                } else {
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
                }
            } else {
                return F(j4 - D());
            }
        }
        return (z) rVar.r(this, j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void K(DataOutput dataOutput) {
        dataOutput.writeInt(this.f4044a);
        dataOutput.writeByte(this.f4045b);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        z zVar = (z) obj;
        int i4 = this.f4044a - zVar.f4044a;
        return i4 == 0 ? this.f4045b - zVar.f4045b : i4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof z) {
            z zVar = (z) obj;
            return this.f4044a == zVar.f4044a && this.f4045b == zVar.f4045b;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.YEAR || rVar == j$.time.temporal.a.MONTH_OF_YEAR || rVar == j$.time.temporal.a.PROLEPTIC_MONTH || rVar == j$.time.temporal.a.YEAR_OF_ERA || rVar == j$.time.temporal.a.ERA : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        return (this.f4045b << 27) ^ this.f4044a;
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return m(rVar).a(r(rVar), rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        return (z) AbstractC0491i.a(iVar, this);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.YEAR_OF_ERA) {
            return j$.time.temporal.w.j(1L, this.f4044a <= 0 ? 1000000000L : 999999999L);
        }
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = y.f4042a[((j$.time.temporal.a) rVar).ordinal()];
            if (i4 != 1) {
                if (i4 != 2) {
                    int i5 = this.f4044a;
                    if (i4 == 3) {
                        if (i5 < 1) {
                            i5 = 1 - i5;
                        }
                        return i5;
                    } else if (i4 != 4) {
                        if (i4 == 5) {
                            return i5 < 1 ? 0 : 1;
                        }
                        throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
                    } else {
                        return i5;
                    }
                }
                return D();
            }
            return this.f4045b;
        }
        return rVar.l(this);
    }

    public final String toString() {
        int i4;
        int i5 = this.f4044a;
        int abs = Math.abs(i5);
        StringBuilder sb = new StringBuilder(9);
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
            sb.append(i5);
        }
        int i6 = this.f4045b;
        sb.append(i6 < 10 ? "-0" : "-");
        sb.append(i6);
        return sb.toString();
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.e() ? j$.time.chrono.u.f3906d : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.MONTHS : j$.time.temporal.n.c(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        if (((AbstractC0483a) AbstractC0491i.p(mVar)).equals(j$.time.chrono.u.f3906d)) {
            return mVar.d(D(), j$.time.temporal.a.PROLEPTIC_MONTH);
        }
        throw new RuntimeException("Adjustment only supported on ISO date-time");
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
