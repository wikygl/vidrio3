package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0509l {

    /* renamed from: b  reason: collision with root package name */
    private static final C0509l f4230b = new C0509l();

    /* renamed from: a  reason: collision with root package name */
    private final Object f4231a;

    private C0509l() {
        this.f4231a = null;
    }

    private C0509l(Object obj) {
        this.f4231a = Objects.requireNonNull(obj);
    }

    public static C0509l a() {
        return f4230b;
    }

    public static C0509l d(Object obj) {
        return new C0509l(obj);
    }

    public final Object b() {
        Object obj = this.f4231a;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException("No value present");
    }

    public final boolean c() {
        return this.f4231a != null;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof C0509l) {
            return Objects.equals(this.f4231a, ((C0509l) obj).f4231a);
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hashCode(this.f4231a);
    }

    public final String toString() {
        Object obj = this.f4231a;
        return obj != null ? String.format("Optional[%s]", obj) : "Optional.empty";
    }
}
