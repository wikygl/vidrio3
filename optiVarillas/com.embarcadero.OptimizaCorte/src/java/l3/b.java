package l3;

import java.io.Serializable;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b<A, B> implements Serializable {

    /* renamed from: j  reason: collision with root package name */
    public final A f5264j;

    /* renamed from: k  reason: collision with root package name */
    public final B f5265k;

    public b(A a4, B b4) {
        this.f5264j = a4;
        this.f5265k = b4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (h.a(this.f5264j, bVar.f5264j) && h.a(this.f5265k, bVar.f5265k)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int i4 = 0;
        A a4 = this.f5264j;
        if (a4 == null) {
            hashCode = 0;
        } else {
            hashCode = a4.hashCode();
        }
        int i5 = hashCode * 31;
        B b4 = this.f5265k;
        if (b4 != null) {
            i4 = b4.hashCode();
        }
        return i5 + i4;
    }

    public final String toString() {
        return "(" + this.f5264j + ", " + this.f5265k + ')';
    }
}
