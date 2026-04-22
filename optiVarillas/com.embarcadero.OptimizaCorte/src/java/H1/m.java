package h1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class m extends s {

    /* renamed from: a  reason: collision with root package name */
    public final long f3573a;

    public m(long j4) {
        this.f3573a = j4;
    }

    @Override // h1.s
    public final long b() {
        return this.f3573a;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof s) && this.f3573a == ((s) obj).b()) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f3573a;
        return 1000003 ^ ((int) ((j4 >>> 32) ^ j4));
    }

    public final String toString() {
        return "LogResponse{nextRequestWaitMillis=" + this.f3573a + "}";
    }
}
