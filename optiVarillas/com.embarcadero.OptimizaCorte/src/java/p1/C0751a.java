package p1;

import com.google.android.gms.internal.ads.gI;

/* renamed from: p1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0751a extends e {

    /* renamed from: b  reason: collision with root package name */
    public final long f5507b;

    /* renamed from: c  reason: collision with root package name */
    public final int f5508c;

    /* renamed from: d  reason: collision with root package name */
    public final int f5509d;

    /* renamed from: e  reason: collision with root package name */
    public final long f5510e;
    public final int f;

    public C0751a(long j4, int i4, int i5, long j5, int i6) {
        this.f5507b = j4;
        this.f5508c = i4;
        this.f5509d = i5;
        this.f5510e = j5;
        this.f = i6;
    }

    @Override // p1.e
    public final int a() {
        return this.f5509d;
    }

    @Override // p1.e
    public final long b() {
        return this.f5510e;
    }

    @Override // p1.e
    public final int c() {
        return this.f5508c;
    }

    @Override // p1.e
    public final int d() {
        return this.f;
    }

    @Override // p1.e
    public final long e() {
        return this.f5507b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof e)) {
            return false;
        }
        e eVar = (e) obj;
        if (this.f5507b == eVar.e() && this.f5508c == eVar.c() && this.f5509d == eVar.a() && this.f5510e == eVar.b() && this.f == eVar.d()) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f5507b;
        long j5 = this.f5510e;
        return this.f ^ ((((((((((int) (j4 ^ (j4 >>> 32))) ^ 1000003) * 1000003) ^ this.f5508c) * 1000003) ^ this.f5509d) * 1000003) ^ ((int) ((j5 >>> 32) ^ j5))) * 1000003);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("EventStoreConfig{maxStorageSizeInBytes=");
        sb.append(this.f5507b);
        sb.append(", loadBatchSize=");
        sb.append(this.f5508c);
        sb.append(", criticalSectionEnterTimeoutMs=");
        sb.append(this.f5509d);
        sb.append(", eventCleanUpAge=");
        sb.append(this.f5510e);
        sb.append(", maxBlobByteSizePerRow=");
        return gI.a(sb, this.f, "}");
    }
}
