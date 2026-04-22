package h1;

import h1.t;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class n extends t {

    /* renamed from: a  reason: collision with root package name */
    public final t.b f3574a;

    /* renamed from: b  reason: collision with root package name */
    public final t.a f3575b;

    public n(t.b bVar, t.a aVar) {
        this.f3574a = bVar;
        this.f3575b = aVar;
    }

    @Override // h1.t
    public final t.a a() {
        return this.f3575b;
    }

    @Override // h1.t
    public final t.b b() {
        return this.f3574a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof t)) {
            return false;
        }
        t tVar = (t) obj;
        t.b bVar = this.f3574a;
        if (bVar != null ? bVar.equals(tVar.b()) : tVar.b() == null) {
            t.a aVar = this.f3575b;
            if (aVar == null) {
                if (tVar.a() == null) {
                    return true;
                }
            } else if (aVar.equals(tVar.a())) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int i4 = 0;
        t.b bVar = this.f3574a;
        if (bVar == null) {
            hashCode = 0;
        } else {
            hashCode = bVar.hashCode();
        }
        int i5 = (hashCode ^ 1000003) * 1000003;
        t.a aVar = this.f3575b;
        if (aVar != null) {
            i4 = aVar.hashCode();
        }
        return i4 ^ i5;
    }

    public final String toString() {
        return "NetworkConnectionInfo{networkType=" + this.f3574a + ", mobileSubtype=" + this.f3575b + "}";
    }
}
