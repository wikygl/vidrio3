package j$.time;

import j$.time.chrono.AbstractC0491i;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class m implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {

    /* renamed from: e  reason: collision with root package name */
    public static final m f3983e;
    public static final m f;

    /* renamed from: g  reason: collision with root package name */
    public static final m f3984g;

    /* renamed from: h  reason: collision with root package name */
    private static final m[] f3985h = new m[24];
    private static final long serialVersionUID = 6414437269572265201L;

    /* renamed from: a  reason: collision with root package name */
    private final byte f3986a;

    /* renamed from: b  reason: collision with root package name */
    private final byte f3987b;

    /* renamed from: c  reason: collision with root package name */
    private final byte f3988c;

    /* renamed from: d  reason: collision with root package name */
    private final int f3989d;

    static {
        int i4 = 0;
        while (true) {
            m[] mVarArr = f3985h;
            if (i4 >= mVarArr.length) {
                m mVar = mVarArr[0];
                f3984g = mVar;
                m mVar2 = mVarArr[12];
                f3983e = mVar;
                f = new m(23, 59, 59, 999999999);
                return;
            }
            mVarArr[i4] = new m(i4, 0, 0, 0);
            i4++;
        }
    }

    private m(int i4, int i5, int i6, int i7) {
        this.f3986a = (byte) i4;
        this.f3987b = (byte) i5;
        this.f3988c = (byte) i6;
        this.f3989d = i7;
    }

    private static m E(int i4, int i5, int i6, int i7) {
        return ((i5 | i6) | i7) == 0 ? f3985h[i4] : new m(i4, i5, i6, i7);
    }

    public static m F(j$.time.temporal.o oVar) {
        Objects.requireNonNull(oVar, "temporal");
        m mVar = (m) oVar.u(j$.time.temporal.n.g());
        if (mVar != null) {
            return mVar;
        }
        String name = oVar.getClass().getName();
        throw new RuntimeException("Unable to obtain LocalTime from TemporalAccessor: " + oVar + " of type " + name);
    }

    private int G(j$.time.temporal.r rVar) {
        int i4 = l.f3981a[((j$.time.temporal.a) rVar).ordinal()];
        byte b4 = this.f3987b;
        int i5 = this.f3989d;
        byte b5 = this.f3986a;
        switch (i4) {
            case 1:
                return i5;
            case 2:
                throw new RuntimeException("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return i5 / 1000;
            case 4:
                throw new RuntimeException("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
            case 5:
                return i5 / 1000000;
            case 6:
                return (int) (T() / 1000000);
            case 7:
                return this.f3988c;
            case 8:
                return U();
            case 9:
                return b4;
            case 10:
                return (b5 * 60) + b4;
            case 11:
                return b5 % 12;
            case 12:
                int i6 = b5 % 12;
                if (i6 % 12 == 0) {
                    return 12;
                }
                return i6;
            case 13:
                return b5;
            case 14:
                if (b5 == 0) {
                    return 24;
                }
                return b5;
            case 15:
                return b5 / 12;
            default:
                throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
    }

    public static m K(int i4) {
        j$.time.temporal.a.HOUR_OF_DAY.D(i4);
        return f3985h[i4];
    }

    public static m L(long j4) {
        j$.time.temporal.a.NANO_OF_DAY.D(j4);
        int i4 = (int) (j4 / 3600000000000L);
        long j5 = j4 - (i4 * 3600000000000L);
        int i5 = (int) (j5 / 60000000000L);
        long j6 = j5 - (i5 * 60000000000L);
        int i6 = (int) (j6 / 1000000000);
        return E(i4, i5, i6, (int) (j6 - (i6 * 1000000000)));
    }

    public static m M(long j4) {
        j$.time.temporal.a.SECOND_OF_DAY.D(j4);
        int i4 = (int) (j4 / 3600);
        long j5 = j4 - (i4 * 3600);
        int i5 = (int) (j5 / 60);
        return E(i4, i5, (int) (j5 - (i5 * 60)), 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v2, types: [int] */
    /* JADX WARN: Type inference failed for: r7v3, types: [int] */
    public static m S(DataInput dataInput) {
        byte b4;
        int i4;
        byte b5;
        int readByte = dataInput.readByte();
        byte b6 = 0;
        if (readByte >= 0) {
            byte readByte2 = dataInput.readByte();
            if (readByte2 < 0) {
                ?? r7 = ~readByte2;
                i4 = 0;
                b6 = r7;
                b4 = 0;
            } else {
                byte readByte3 = dataInput.readByte();
                if (readByte3 < 0) {
                    b6 = readByte2;
                    b5 = ~readByte3;
                } else {
                    int readInt = dataInput.readInt();
                    b4 = readByte3;
                    i4 = readInt;
                    b6 = readByte2;
                }
            }
            j$.time.temporal.a.HOUR_OF_DAY.D(readByte);
            j$.time.temporal.a.MINUTE_OF_HOUR.D(b6);
            j$.time.temporal.a.SECOND_OF_MINUTE.D(b4);
            j$.time.temporal.a.NANO_OF_SECOND.D(i4);
            return E(readByte, b6, b4, i4);
        }
        readByte = ~readByte;
        b5 = 0;
        i4 = 0;
        b4 = b5;
        j$.time.temporal.a.HOUR_OF_DAY.D(readByte);
        j$.time.temporal.a.MINUTE_OF_HOUR.D(b6);
        j$.time.temporal.a.SECOND_OF_MINUTE.D(b4);
        j$.time.temporal.a.NANO_OF_SECOND.D(i4);
        return E(readByte, b6, b4, i4);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 4, this);
    }

    @Override // java.lang.Comparable
    /* renamed from: D */
    public final int compareTo(m mVar) {
        int compare = Integer.compare(this.f3986a, mVar.f3986a);
        if (compare == 0) {
            int compare2 = Integer.compare(this.f3987b, mVar.f3987b);
            if (compare2 == 0) {
                int compare3 = Integer.compare(this.f3988c, mVar.f3988c);
                return compare3 == 0 ? Integer.compare(this.f3989d, mVar.f3989d) : compare3;
            }
            return compare2;
        }
        return compare;
    }

    public final int H() {
        return this.f3986a;
    }

    public final int I() {
        return this.f3989d;
    }

    public final int J() {
        return this.f3988c;
    }

    @Override // j$.time.temporal.m
    /* renamed from: N */
    public final m e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            switch (l.f3982b[((j$.time.temporal.b) uVar).ordinal()]) {
                case 1:
                    return Q(j4);
                case 2:
                    return Q((j4 % 86400000000L) * 1000);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return Q((j4 % 86400000) * 1000000);
                case 4:
                    return R(j4);
                case 5:
                    return P(j4);
                case 6:
                    return O(j4);
                case 7:
                    return O((j4 % 2) * 12);
                default:
                    throw new RuntimeException("Unsupported unit: " + uVar);
            }
        }
        return (m) uVar.j(this, j4);
    }

    public final m O(long j4) {
        if (j4 == 0) {
            return this;
        }
        return E(((((int) (j4 % 24)) + this.f3986a) + 24) % 24, this.f3987b, this.f3988c, this.f3989d);
    }

    public final m P(long j4) {
        if (j4 == 0) {
            return this;
        }
        int i4 = (this.f3986a * 60) + this.f3987b;
        int i5 = ((((int) (j4 % 1440)) + i4) + 1440) % 1440;
        return i4 == i5 ? this : E(i5 / 60, i5 % 60, this.f3988c, this.f3989d);
    }

    public final m Q(long j4) {
        if (j4 == 0) {
            return this;
        }
        long T3 = T();
        long j5 = (((j4 % 86400000000000L) + T3) + 86400000000000L) % 86400000000000L;
        return T3 == j5 ? this : E((int) (j5 / 3600000000000L), (int) ((j5 / 60000000000L) % 60), (int) ((j5 / 1000000000) % 60), (int) (j5 % 1000000000));
    }

    public final m R(long j4) {
        if (j4 == 0) {
            return this;
        }
        int i4 = (this.f3987b * 60) + (this.f3986a * 3600) + this.f3988c;
        int i5 = ((((int) (j4 % 86400)) + i4) + 86400) % 86400;
        return i4 == i5 ? this : E(i5 / 3600, (i5 / 60) % 60, i5 % 60, this.f3989d);
    }

    public final long T() {
        return (this.f3988c * 1000000000) + (this.f3987b * 60000000000L) + (this.f3986a * 3600000000000L) + this.f3989d;
    }

    public final int U() {
        return (this.f3987b * 60) + (this.f3986a * 3600) + this.f3988c;
    }

    @Override // j$.time.temporal.m
    /* renamed from: V */
    public final m d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            aVar.D(j4);
            int i4 = l.f3981a[aVar.ordinal()];
            byte b4 = this.f3987b;
            byte b5 = this.f3988c;
            int i5 = this.f3989d;
            byte b6 = this.f3986a;
            switch (i4) {
                case 1:
                    return W((int) j4);
                case 2:
                    return L(j4);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return W(((int) j4) * 1000);
                case 4:
                    return L(j4 * 1000);
                case 5:
                    return W(((int) j4) * 1000000);
                case 6:
                    return L(j4 * 1000000);
                case 7:
                    int i6 = (int) j4;
                    if (b5 == i6) {
                        return this;
                    }
                    j$.time.temporal.a.SECOND_OF_MINUTE.D(i6);
                    return E(b6, b4, i6, i5);
                case 8:
                    return R(j4 - U());
                case 9:
                    int i7 = (int) j4;
                    if (b4 == i7) {
                        return this;
                    }
                    j$.time.temporal.a.MINUTE_OF_HOUR.D(i7);
                    return E(b6, i7, b5, i5);
                case 10:
                    return P(j4 - ((b6 * 60) + b4));
                case 11:
                    return O(j4 - (b6 % 12));
                case 12:
                    if (j4 == 12) {
                        j4 = 0;
                    }
                    return O(j4 - (b6 % 12));
                case 13:
                    int i8 = (int) j4;
                    if (b6 == i8) {
                        return this;
                    }
                    j$.time.temporal.a.HOUR_OF_DAY.D(i8);
                    return E(i8, b4, b5, i5);
                case 14:
                    if (j4 == 24) {
                        j4 = 0;
                    }
                    int i9 = (int) j4;
                    if (b6 == i9) {
                        return this;
                    }
                    j$.time.temporal.a.HOUR_OF_DAY.D(i9);
                    return E(i9, b4, b5, i5);
                case 15:
                    return O((j4 - (b6 / 12)) * 12);
                default:
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            }
        }
        return (m) rVar.r(this, j4);
    }

    public final m W(int i4) {
        if (this.f3989d == i4) {
            return this;
        }
        j$.time.temporal.a.NANO_OF_SECOND.D(i4);
        return E(this.f3986a, this.f3987b, this.f3988c, i4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void X(DataOutput dataOutput) {
        int i4;
        byte b4 = this.f3988c;
        byte b5 = this.f3986a;
        byte b6 = this.f3987b;
        int i5 = this.f3989d;
        if (i5 != 0) {
            dataOutput.writeByte(b5);
            dataOutput.writeByte(b6);
            dataOutput.writeByte(b4);
            dataOutput.writeInt(i5);
            return;
        }
        if (b4 != 0) {
            dataOutput.writeByte(b5);
            dataOutput.writeByte(b6);
            i4 = ~b4;
        } else if (b6 == 0) {
            i4 = ~b5;
        } else {
            dataOutput.writeByte(b5);
            i4 = ~b6;
        }
        dataOutput.writeByte(i4);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof m) {
            m mVar = (m) obj;
            return this.f3986a == mVar.f3986a && this.f3987b == mVar.f3987b && this.f3988c == mVar.f3988c && this.f3989d == mVar.f3989d;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        long T3 = T();
        return (int) (T3 ^ (T3 >>> 32));
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? G(rVar) : j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        boolean z4 = iVar instanceof m;
        Object obj = iVar;
        if (!z4) {
            obj = AbstractC0491i.a(iVar, this);
        }
        return (m) obj;
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.NANO_OF_DAY ? T() : rVar == j$.time.temporal.a.MICRO_OF_DAY ? T() / 1000 : G(rVar) : rVar.l(this);
    }

    public final String toString() {
        int i4;
        StringBuilder sb = new StringBuilder(18);
        byte b4 = this.f3986a;
        sb.append(b4 < 10 ? "0" : "");
        sb.append((int) b4);
        byte b5 = this.f3987b;
        sb.append(b5 < 10 ? ":0" : ":");
        sb.append((int) b5);
        byte b6 = this.f3988c;
        int i5 = this.f3989d;
        if (b6 > 0 || i5 > 0) {
            sb.append(b6 < 10 ? ":0" : ":");
            sb.append((int) b6);
            if (i5 > 0) {
                sb.append('.');
                int i6 = 1000000;
                if (i5 % 1000000 == 0) {
                    i4 = (i5 / 1000000) + 1000;
                } else {
                    if (i5 % 1000 == 0) {
                        i5 /= 1000;
                    } else {
                        i6 = 1000000000;
                    }
                    i4 = i5 + i6;
                }
                sb.append(Integer.toString(i4).substring(1));
            }
        }
        return sb.toString();
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.e() || tVar == j$.time.temporal.n.k() || tVar == j$.time.temporal.n.j() || tVar == j$.time.temporal.n.h()) {
            return null;
        }
        if (tVar == j$.time.temporal.n.g()) {
            return this;
        }
        if (tVar == j$.time.temporal.n.f()) {
            return null;
        }
        return tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.NANOS : tVar.a(this);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(T(), j$.time.temporal.a.NANO_OF_DAY);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
