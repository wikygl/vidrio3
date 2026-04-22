package h1;

import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class i extends o {

    /* renamed from: a  reason: collision with root package name */
    public final List<r> f3552a;

    public i(ArrayList arrayList) {
        this.f3552a = arrayList;
    }

    @Override // h1.o
    public final List<r> a() {
        return this.f3552a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof o) {
            return this.f3552a.equals(((o) obj).a());
        }
        return false;
    }

    public final int hashCode() {
        return this.f3552a.hashCode() ^ 1000003;
    }

    public final String toString() {
        return "BatchedLogRequest{logRequests=" + this.f3552a + "}";
    }
}
