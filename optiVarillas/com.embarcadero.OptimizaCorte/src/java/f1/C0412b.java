package f1;

/* renamed from: f1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0412b {

    /* renamed from: a  reason: collision with root package name */
    public final String f3392a;

    public C0412b(String str) {
        if (str != null) {
            this.f3392a = str;
            return;
        }
        throw new NullPointerException("name is null");
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0412b)) {
            return false;
        }
        return this.f3392a.equals(((C0412b) obj).f3392a);
    }

    public final int hashCode() {
        return this.f3392a.hashCode() ^ 1000003;
    }

    public final String toString() {
        return C.b.c(new StringBuilder("Encoding{name=\""), this.f3392a, "\"}");
    }
}
