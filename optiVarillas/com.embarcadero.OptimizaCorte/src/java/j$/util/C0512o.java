package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0512o {

    /* renamed from: c  reason: collision with root package name */
    private static final C0512o f4238c = new C0512o();

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4239a;

    /* renamed from: b  reason: collision with root package name */
    private final long f4240b;

    private C0512o() {
        this.f4239a = false;
        this.f4240b = 0L;
    }

    private C0512o(long j4) {
        this.f4239a = true;
        this.f4240b = j4;
    }

    public static C0512o a() {
        return f4238c;
    }

    public static C0512o d(long j4) {
        return new C0512o(j4);
    }

    public final long b() {
        if (this.f4239a) {
            return this.f4240b;
        }
        throw new NoSuchElementException("No value present");
    }

    public final boolean c() {
        return this.f4239a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof C0512o) {
            C0512o c0512o = (C0512o) obj;
            boolean z4 = this.f4239a;
            if (z4 && c0512o.f4239a) {
                if (this.f4240b == c0512o.f4240b) {
                    return true;
                }
            } else if (z4 == c0512o.f4239a) {
                return true;
            }
            return false;
        }
        return false;
    }

    public final int hashCode() {
        if (this.f4239a) {
            long j4 = this.f4240b;
            return (int) (j4 ^ (j4 >>> 32));
        }
        return 0;
    }

    public final String toString() {
        if (this.f4239a) {
            return "OptionalLong[" + this.f4240b + "]";
        }
        return "OptionalLong.empty";
    }
}
