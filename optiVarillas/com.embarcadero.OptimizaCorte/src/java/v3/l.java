package v3;

import C3.C;
import v3.a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class l extends a implements z3.a {

    /* renamed from: p  reason: collision with root package name */
    public final boolean f6313p;

    public l(Object obj) {
        super(obj, C.class, "classSimpleName", "getClassSimpleName(Ljava/lang/Object;)Ljava/lang/String;", true);
        this.f6313p = false;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof l) {
            l lVar = (l) obj;
            if (d().equals(lVar.d()) && this.f6302m.equals(lVar.f6302m) && this.f6303n.equals(lVar.f6303n) && h.a(this.f6300k, lVar.f6300k)) {
                return true;
            }
            return false;
        } else if (!(obj instanceof l)) {
            return false;
        } else {
            return obj.equals(h());
        }
    }

    public final z3.a h() {
        if (this.f6313p) {
            return this;
        }
        z3.a aVar = this.f6299j;
        if (aVar == null) {
            z3.a a4 = a();
            this.f6299j = a4;
            return a4;
        }
        return aVar;
    }

    public final int hashCode() {
        int hashCode = this.f6302m.hashCode();
        return this.f6303n.hashCode() + ((hashCode + (d().hashCode() * 31)) * 31);
    }

    public final String toString() {
        z3.a h4 = h();
        if (h4 != this) {
            return h4.toString();
        }
        return C.b.c(new StringBuilder("property "), this.f6302m, " (Kotlin reflection is not available)");
    }

    public l() {
        super(a.C0077a.f6305j, null, null, null, false);
        this.f6313p = false;
    }
}
