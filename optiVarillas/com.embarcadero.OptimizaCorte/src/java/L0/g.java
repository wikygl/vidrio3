package L0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class g {

    /* renamed from: a  reason: collision with root package name */
    public final String f1435a;

    /* renamed from: b  reason: collision with root package name */
    public final int f1436b;

    public g(String str, int i4) {
        this.f1435a = str;
        this.f1436b = i4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof g)) {
            return false;
        }
        g gVar = (g) obj;
        if (this.f1436b != gVar.f1436b) {
            return false;
        }
        return this.f1435a.equals(gVar.f1435a);
    }

    public final int hashCode() {
        return (this.f1435a.hashCode() * 31) + this.f1436b;
    }
}
