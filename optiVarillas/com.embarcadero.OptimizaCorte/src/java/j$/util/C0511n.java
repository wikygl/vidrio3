package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0511n {

    /* renamed from: c  reason: collision with root package name */
    private static final C0511n f4235c = new C0511n();

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4236a;

    /* renamed from: b  reason: collision with root package name */
    private final int f4237b;

    private C0511n() {
        this.f4236a = false;
        this.f4237b = 0;
    }

    private C0511n(int i4) {
        this.f4236a = true;
        this.f4237b = i4;
    }

    public static C0511n a() {
        return f4235c;
    }

    public static C0511n d(int i4) {
        return new C0511n(i4);
    }

    public final int b() {
        if (this.f4236a) {
            return this.f4237b;
        }
        throw new NoSuchElementException("No value present");
    }

    public final boolean c() {
        return this.f4236a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof C0511n) {
            C0511n c0511n = (C0511n) obj;
            boolean z4 = this.f4236a;
            if (z4 && c0511n.f4236a) {
                if (this.f4237b == c0511n.f4237b) {
                    return true;
                }
            } else if (z4 == c0511n.f4236a) {
                return true;
            }
            return false;
        }
        return false;
    }

    public final int hashCode() {
        if (this.f4236a) {
            return this.f4237b;
        }
        return 0;
    }

    public final String toString() {
        if (this.f4236a) {
            return "OptionalInt[" + this.f4237b + "]";
        }
        return "OptionalInt.empty";
    }
}
