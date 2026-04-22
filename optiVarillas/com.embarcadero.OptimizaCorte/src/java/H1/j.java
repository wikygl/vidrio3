package h1;

import h1.p;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j extends p {

    /* renamed from: a  reason: collision with root package name */
    public final p.a f3553a = p.a.f3576j;

    /* renamed from: b  reason: collision with root package name */
    public final AbstractC0434a f3554b;

    public j(h hVar) {
        this.f3554b = hVar;
    }

    @Override // h1.p
    public final AbstractC0434a a() {
        return this.f3554b;
    }

    @Override // h1.p
    public final p.a b() {
        return this.f3553a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof p)) {
            return false;
        }
        p pVar = (p) obj;
        p.a aVar = this.f3553a;
        if (aVar != null ? aVar.equals(pVar.b()) : pVar.b() == null) {
            AbstractC0434a abstractC0434a = this.f3554b;
            if (abstractC0434a == null) {
                if (pVar.a() == null) {
                    return true;
                }
            } else if (abstractC0434a.equals(pVar.a())) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int i4 = 0;
        p.a aVar = this.f3553a;
        if (aVar == null) {
            hashCode = 0;
        } else {
            hashCode = aVar.hashCode();
        }
        int i5 = (hashCode ^ 1000003) * 1000003;
        AbstractC0434a abstractC0434a = this.f3554b;
        if (abstractC0434a != null) {
            i4 = abstractC0434a.hashCode();
        }
        return i4 ^ i5;
    }

    public final String toString() {
        return "ClientInfo{clientType=" + this.f3553a + ", androidClientInfo=" + this.f3554b + "}";
    }
}
