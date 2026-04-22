package l3;

import java.io.Serializable;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c<T> implements Serializable {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a implements Serializable {

        /* renamed from: j  reason: collision with root package name */
        public final Throwable f5266j;

        public a(Throwable th) {
            h.e(th, "exception");
            this.f5266j = th;
        }

        public final boolean equals(Object obj) {
            if (obj instanceof a) {
                if (h.a(this.f5266j, ((a) obj).f5266j)) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            return this.f5266j.hashCode();
        }

        public final String toString() {
            return "Failure(" + this.f5266j + ')';
        }
    }

    public static final Throwable a(Object obj) {
        if (obj instanceof a) {
            return ((a) obj).f5266j;
        }
        return null;
    }
}
