package j$.time;

import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class Duration implements Comparable<Duration>, Serializable {

    /* renamed from: c  reason: collision with root package name */
    public static final Duration f3846c = new Duration(0, 0);
    private static final long serialVersionUID = 3078945930695997490L;

    /* renamed from: a  reason: collision with root package name */
    private final long f3847a;

    /* renamed from: b  reason: collision with root package name */
    private final int f3848b;

    static {
        BigInteger.valueOf(1000000000L);
    }

    private Duration(long j4, int i4) {
        this.f3847a = j4;
        this.f3848b = i4;
    }

    private static Duration j(long j4, int i4) {
        return (((long) i4) | j4) == 0 ? f3846c : new Duration(j4, i4);
    }

    public static Duration ofMillis(long j4) {
        long j5 = j4 / 1000;
        int i4 = (int) (j4 % 1000);
        if (i4 < 0) {
            i4 += 1000;
            j5--;
        }
        return j(j5, i4 * 1000000);
    }

    public static Duration r(long j4) {
        return j(j4, 0);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public static Duration u(long j4, long j5) {
        return j(j$.com.android.tools.r8.a.i(j4, j$.com.android.tools.r8.a.n(j5, 1000000000L)), (int) j$.com.android.tools.r8.a.m(j5, 1000000000L));
    }

    private Object writeReplace() {
        return new v((byte) 1, this);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Duration duration) {
        Duration duration2 = duration;
        int compare = Long.compare(this.f3847a, duration2.f3847a);
        return compare != 0 ? compare : this.f3848b - duration2.f3848b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Duration) {
            Duration duration = (Duration) obj;
            return this.f3847a == duration.f3847a && this.f3848b == duration.f3848b;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f3847a;
        return (this.f3848b * 51) + ((int) (j4 ^ (j4 >>> 32)));
    }

    public final int l() {
        return this.f3848b;
    }

    public final long m() {
        return this.f3847a;
    }

    public long toMillis() {
        long j4 = this.f3848b;
        long j5 = this.f3847a;
        if (j5 < 0) {
            j5++;
            j4 -= 1000000000;
        }
        return j$.com.android.tools.r8.a.i(j$.com.android.tools.r8.a.o(j5, 1000), j4 / 1000000);
    }

    public final String toString() {
        if (this == f3846c) {
            return "PT0S";
        }
        long j4 = this.f3847a;
        int i4 = this.f3848b;
        long j5 = (j4 >= 0 || i4 <= 0) ? j4 : 1 + j4;
        long j6 = j5 / 3600;
        int i5 = (int) ((j5 % 3600) / 60);
        int i6 = (int) (j5 % 60);
        StringBuilder sb = new StringBuilder(24);
        sb.append("PT");
        if (j6 != 0) {
            sb.append(j6);
            sb.append('H');
        }
        if (i5 != 0) {
            sb.append(i5);
            sb.append('M');
        }
        if (i6 == 0 && i4 == 0 && sb.length() > 2) {
            return sb.toString();
        }
        if (j4 >= 0 || i4 <= 0 || i6 != 0) {
            sb.append(i6);
        } else {
            sb.append("-0");
        }
        if (i4 > 0) {
            int length = sb.length();
            sb.append(j4 < 0 ? 2000000000 - i4 : i4 + 1000000000);
            while (sb.charAt(sb.length() - 1) == '0') {
                sb.setLength(sb.length() - 1);
            }
            sb.setCharAt(length, '.');
        }
        sb.append('S');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void v(DataOutput dataOutput) {
        dataOutput.writeLong(this.f3847a);
        dataOutput.writeInt(this.f3848b);
    }
}
