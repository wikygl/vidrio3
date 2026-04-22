package L0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public final String f1431a;

    /* renamed from: b  reason: collision with root package name */
    public final Long f1432b;

    public d(String str, long j4) {
        this.f1431a = str;
        this.f1432b = Long.valueOf(j4);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        if (!this.f1431a.equals(dVar.f1431a)) {
            return false;
        }
        Long l2 = dVar.f1432b;
        Long l4 = this.f1432b;
        if (l4 != null) {
            return l4.equals(l2);
        }
        if (l2 == null) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int i4;
        int hashCode = this.f1431a.hashCode() * 31;
        Long l2 = this.f1432b;
        if (l2 != null) {
            i4 = l2.hashCode();
        } else {
            i4 = 0;
        }
        return hashCode + i4;
    }
}
