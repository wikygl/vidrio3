package y3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c extends a {
    static {
        new a(1, 0, 1);
    }

    public final boolean equals(Object obj) {
        if (obj instanceof c) {
            if (!isEmpty() || !((c) obj).isEmpty()) {
                c cVar = (c) obj;
                if (this.f6515j == cVar.f6515j) {
                    if (this.f6516k == cVar.f6516k) {
                    }
                }
            }
            return true;
        }
        return false;
    }

    public final int hashCode() {
        if (isEmpty()) {
            return -1;
        }
        return (this.f6515j * 31) + this.f6516k;
    }

    public final boolean isEmpty() {
        if (this.f6515j > this.f6516k) {
            return true;
        }
        return false;
    }

    public final String toString() {
        return this.f6515j + ".." + this.f6516k;
    }
}
