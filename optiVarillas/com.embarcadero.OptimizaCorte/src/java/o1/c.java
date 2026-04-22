package o1;

import java.util.Set;
import o1.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c extends f.a {

    /* renamed from: a  reason: collision with root package name */
    public final long f5429a;

    /* renamed from: b  reason: collision with root package name */
    public final long f5430b;

    /* renamed from: c  reason: collision with root package name */
    public final Set<f.b> f5431c;

    public c(long j4, long j5, Set set) {
        this.f5429a = j4;
        this.f5430b = j5;
        this.f5431c = set;
    }

    @Override // o1.f.a
    public final long a() {
        return this.f5429a;
    }

    @Override // o1.f.a
    public final Set<f.b> b() {
        return this.f5431c;
    }

    @Override // o1.f.a
    public final long c() {
        return this.f5430b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof f.a)) {
            return false;
        }
        f.a aVar = (f.a) obj;
        if (this.f5429a == aVar.a() && this.f5430b == aVar.c() && this.f5431c.equals(aVar.b())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f5429a;
        long j5 = this.f5430b;
        return this.f5431c.hashCode() ^ ((((((int) (j4 ^ (j4 >>> 32))) ^ 1000003) * 1000003) ^ ((int) ((j5 >>> 32) ^ j5))) * 1000003);
    }

    public final String toString() {
        return "ConfigValue{delta=" + this.f5429a + ", maxAllowedDelay=" + this.f5430b + ", flags=" + this.f5431c + "}";
    }
}
