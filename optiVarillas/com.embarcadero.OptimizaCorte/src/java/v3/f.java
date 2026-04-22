package v3;

import androidx.activity.OnBackPressedDispatcher;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class f extends a implements e, z3.a, l3.a {

    /* renamed from: p  reason: collision with root package name */
    public final int f6309p;

    /* renamed from: q  reason: collision with root package name */
    public final int f6310q;

    public f(Object obj) {
        super(obj, OnBackPressedDispatcher.class, "updateEnabledCallbacks", "updateEnabledCallbacks()V", false);
        this.f6309p = 0;
        this.f6310q = 0;
    }

    @Override // v3.a
    public final z3.a a() {
        n.f6315a.getClass();
        return this;
    }

    @Override // v3.e
    public final int e() {
        return this.f6309p;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof f) {
            f fVar = (f) obj;
            if (this.f6302m.equals(fVar.f6302m) && this.f6303n.equals(fVar.f6303n) && this.f6310q == fVar.f6310q && this.f6309p == fVar.f6309p && h.a(this.f6300k, fVar.f6300k) && h.a(d(), fVar.d())) {
                return true;
            }
            return false;
        } else if (!(obj instanceof f)) {
            return false;
        } else {
            z3.a aVar = this.f6299j;
            if (aVar == null) {
                a();
                this.f6299j = this;
                aVar = this;
            }
            return obj.equals(aVar);
        }
    }

    public final int hashCode() {
        int hashCode;
        if (d() == null) {
            hashCode = 0;
        } else {
            hashCode = d().hashCode() * 31;
        }
        return this.f6303n.hashCode() + ((this.f6302m.hashCode() + hashCode) * 31);
    }

    public final String toString() {
        z3.a aVar = this.f6299j;
        if (aVar == null) {
            a();
            this.f6299j = this;
            aVar = this;
        }
        if (aVar != this) {
            return aVar.toString();
        }
        String str = this.f6302m;
        if ("<init>".equals(str)) {
            return "constructor (Kotlin reflection is not available)";
        }
        return C.b.b("function ", str, " (Kotlin reflection is not available)");
    }
}
