package j1;

import j1.g;

/* renamed from: j1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0664b extends g {

    /* renamed from: a  reason: collision with root package name */
    public final g.a f4742a;

    /* renamed from: b  reason: collision with root package name */
    public final long f4743b;

    public C0664b(g.a aVar, long j4) {
        this.f4742a = aVar;
        this.f4743b = j4;
    }

    @Override // j1.g
    public final long a() {
        return this.f4743b;
    }

    @Override // j1.g
    public final g.a b() {
        return this.f4742a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof g)) {
            return false;
        }
        g gVar = (g) obj;
        if (this.f4742a.equals(gVar.b()) && this.f4743b == gVar.a()) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f4743b;
        return ((this.f4742a.hashCode() ^ 1000003) * 1000003) ^ ((int) (j4 ^ (j4 >>> 32)));
    }

    public final String toString() {
        return "BackendResponse{status=" + this.f4742a + ", nextRequestWaitMillis=" + this.f4743b + "}";
    }
}
