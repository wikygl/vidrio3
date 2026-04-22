package j$.time.temporal;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class w implements Serializable {
    private static final long serialVersionUID = -7317881728594519368L;

    /* renamed from: a  reason: collision with root package name */
    private final long f4028a;

    /* renamed from: b  reason: collision with root package name */
    private final long f4029b;

    /* renamed from: c  reason: collision with root package name */
    private final long f4030c;

    /* renamed from: d  reason: collision with root package name */
    private final long f4031d;

    private w(long j4, long j5, long j6, long j7) {
        this.f4028a = j4;
        this.f4029b = j5;
        this.f4030c = j6;
        this.f4031d = j7;
    }

    private String c(long j4, r rVar) {
        if (rVar == null) {
            return "Invalid value (valid values " + this + "): " + j4;
        }
        return "Invalid value for " + rVar + " (valid values " + this + "): " + j4;
    }

    public static w j(long j4, long j5) {
        if (j4 <= j5) {
            return new w(j4, j4, j5, j5);
        }
        throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }

    public static w k(long j4, long j5) {
        if (j4 <= j5) {
            if (1 <= j5) {
                return new w(1L, 1L, j4, j5);
            }
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
        throw new IllegalArgumentException("Smallest maximum value must be less than largest maximum value");
    }

    private void readObject(ObjectInputStream objectInputStream) {
        objectInputStream.defaultReadObject();
        long j4 = this.f4028a;
        long j5 = this.f4029b;
        if (j4 > j5) {
            throw new InvalidObjectException("Smallest minimum value must be less than largest minimum value");
        }
        long j6 = this.f4030c;
        long j7 = this.f4031d;
        if (j6 > j7) {
            throw new InvalidObjectException("Smallest maximum value must be less than largest maximum value");
        }
        if (j5 > j7) {
            throw new InvalidObjectException("Minimum value must be less than maximum value");
        }
    }

    public final int a(long j4, r rVar) {
        if (h() && i(j4)) {
            return (int) j4;
        }
        throw new RuntimeException(c(j4, rVar));
    }

    public final void b(long j4, r rVar) {
        if (!i(j4)) {
            throw new RuntimeException(c(j4, rVar));
        }
    }

    public final long d() {
        return this.f4031d;
    }

    public final long e() {
        return this.f4028a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof w) {
            w wVar = (w) obj;
            return this.f4028a == wVar.f4028a && this.f4029b == wVar.f4029b && this.f4030c == wVar.f4030c && this.f4031d == wVar.f4031d;
        }
        return false;
    }

    public final long f() {
        return this.f4030c;
    }

    public final boolean g() {
        return this.f4028a == this.f4029b && this.f4030c == this.f4031d;
    }

    public final boolean h() {
        return this.f4028a >= -2147483648L && this.f4031d <= 2147483647L;
    }

    public final int hashCode() {
        long j4 = this.f4029b;
        long j5 = this.f4028a + (j4 << 16) + (j4 >> 48);
        long j6 = this.f4030c;
        long j7 = j5 + (j6 << 32) + (j6 >> 32);
        long j8 = this.f4031d;
        long j9 = j7 + (j8 << 48) + (j8 >> 16);
        return (int) ((j9 >>> 32) ^ j9);
    }

    public final boolean i(long j4) {
        return j4 >= this.f4028a && j4 <= this.f4031d;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        long j4 = this.f4028a;
        sb.append(j4);
        long j5 = this.f4029b;
        if (j4 != j5) {
            sb.append('/');
            sb.append(j5);
        }
        sb.append(" - ");
        long j6 = this.f4030c;
        sb.append(j6);
        long j7 = this.f4031d;
        if (j6 != j7) {
            sb.append('/');
            sb.append(j7);
        }
        return sb.toString();
    }
}
