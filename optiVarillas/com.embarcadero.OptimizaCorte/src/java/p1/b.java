package p1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends i {

    /* renamed from: a  reason: collision with root package name */
    public final long f5511a;

    /* renamed from: b  reason: collision with root package name */
    public final i1.s f5512b;

    /* renamed from: c  reason: collision with root package name */
    public final i1.n f5513c;

    public b(long j4, i1.s sVar, i1.n nVar) {
        this.f5511a = j4;
        if (sVar != null) {
            this.f5512b = sVar;
            if (nVar != null) {
                this.f5513c = nVar;
                return;
            }
            throw new NullPointerException("Null event");
        }
        throw new NullPointerException("Null transportContext");
    }

    @Override // p1.i
    public final i1.n a() {
        return this.f5513c;
    }

    @Override // p1.i
    public final long b() {
        return this.f5511a;
    }

    @Override // p1.i
    public final i1.s c() {
        return this.f5512b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof i)) {
            return false;
        }
        i iVar = (i) obj;
        if (this.f5511a == iVar.b() && this.f5512b.equals(iVar.c()) && this.f5513c.equals(iVar.a())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f5511a;
        return this.f5513c.hashCode() ^ ((((((int) ((j4 >>> 32) ^ j4)) ^ 1000003) * 1000003) ^ this.f5512b.hashCode()) * 1000003);
    }

    public final String toString() {
        return "PersistedEvent{id=" + this.f5511a + ", transportContext=" + this.f5512b + ", event=" + this.f5513c + "}";
    }
}
