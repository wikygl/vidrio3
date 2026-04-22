package L;

import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b<F, S> {

    /* renamed from: a  reason: collision with root package name */
    public final F f1422a;

    /* renamed from: b  reason: collision with root package name */
    public final S f1423b;

    public b(F f, S s4) {
        this.f1422a = f;
        this.f1423b = s4;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (!Objects.equals(bVar.f1422a, this.f1422a) || !Objects.equals(bVar.f1423b, this.f1423b)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int hashCode;
        int i4 = 0;
        F f = this.f1422a;
        if (f == null) {
            hashCode = 0;
        } else {
            hashCode = f.hashCode();
        }
        S s4 = this.f1423b;
        if (s4 != null) {
            i4 = s4.hashCode();
        }
        return i4 ^ hashCode;
    }

    public final String toString() {
        return "Pair{" + this.f1422a + " " + this.f1423b + "}";
    }
}
