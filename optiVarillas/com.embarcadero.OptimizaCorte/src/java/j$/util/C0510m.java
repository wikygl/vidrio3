package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0510m {

    /* renamed from: c  reason: collision with root package name */
    private static final C0510m f4232c = new C0510m();

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4233a;

    /* renamed from: b  reason: collision with root package name */
    private final double f4234b;

    private C0510m() {
        this.f4233a = false;
        this.f4234b = Double.NaN;
    }

    private C0510m(double d4) {
        this.f4233a = true;
        this.f4234b = d4;
    }

    public static C0510m a() {
        return f4232c;
    }

    public static C0510m d(double d4) {
        return new C0510m(d4);
    }

    public final double b() {
        if (this.f4233a) {
            return this.f4234b;
        }
        throw new NoSuchElementException("No value present");
    }

    public final boolean c() {
        return this.f4233a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof C0510m) {
            C0510m c0510m = (C0510m) obj;
            boolean z4 = this.f4233a;
            if (z4 && c0510m.f4233a) {
                if (Double.compare(this.f4234b, c0510m.f4234b) == 0) {
                    return true;
                }
            } else if (z4 == c0510m.f4233a) {
                return true;
            }
            return false;
        }
        return false;
    }

    public final int hashCode() {
        if (this.f4233a) {
            long doubleToLongBits = Double.doubleToLongBits(this.f4234b);
            return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
        }
        return 0;
    }

    public final String toString() {
        if (this.f4233a) {
            return "OptionalDouble[" + this.f4234b + "]";
        }
        return "OptionalDouble.empty";
    }
}
