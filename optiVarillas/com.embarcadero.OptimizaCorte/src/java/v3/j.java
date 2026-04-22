package v3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j implements b {

    /* renamed from: a  reason: collision with root package name */
    public final Class<?> f6312a;

    public j(Class cls) {
        h.e(cls, "jClass");
        this.f6312a = cls;
    }

    @Override // v3.b
    public final Class<?> a() {
        return this.f6312a;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof j) {
            if (h.a(this.f6312a, ((j) obj).f6312a)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.f6312a.hashCode();
    }

    public final String toString() {
        return this.f6312a.toString() + " (Kotlin reflection is not available)";
    }
}
