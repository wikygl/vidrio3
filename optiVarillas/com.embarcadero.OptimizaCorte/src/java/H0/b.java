package H0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public boolean f1012a;

    /* renamed from: b  reason: collision with root package name */
    public boolean f1013b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f1014c;

    /* renamed from: d  reason: collision with root package name */
    public boolean f1015d;

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (this.f1012a == bVar.f1012a && this.f1013b == bVar.f1013b && this.f1014c == bVar.f1014c && this.f1015d == bVar.f1015d) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [boolean, int] */
    public final int hashCode() {
        boolean z4 = this.f1013b;
        ?? r12 = this.f1012a;
        int i4 = r12;
        if (z4) {
            i4 = r12 + 16;
        }
        int i5 = i4;
        if (this.f1014c) {
            i5 = i4 + 256;
        }
        if (this.f1015d) {
            return i5 + 4096;
        }
        return i5;
    }

    public final String toString() {
        return "[ Connected=" + this.f1012a + " Validated=" + this.f1013b + " Metered=" + this.f1014c + " NotRoaming=" + this.f1015d + " ]";
    }
}
