package i0;

import S0.C0284u0;
import com.google.android.gms.internal.ads.gI;

/* renamed from: i0.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0447c {

    /* renamed from: a  reason: collision with root package name */
    public final long f3596a;

    /* renamed from: b  reason: collision with root package name */
    public final long f3597b;

    /* renamed from: c  reason: collision with root package name */
    public final int f3598c;

    public C0447c(int i4, long j4, long j5) {
        this.f3596a = j4;
        this.f3597b = j5;
        this.f3598c = i4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0447c)) {
            return false;
        }
        C0447c c0447c = (C0447c) obj;
        if (this.f3596a == c0447c.f3596a && this.f3597b == c0447c.f3597b && this.f3598c == c0447c.f3598c) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        long j4 = this.f3596a;
        long j5 = this.f3597b;
        return (((((int) (j4 ^ (j4 >>> 32))) * 31) + ((int) (j5 ^ (j5 >>> 32)))) * 31) + this.f3598c;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("TaxonomyVersion=");
        sb.append(this.f3596a);
        sb.append(", ModelVersion=");
        sb.append(this.f3597b);
        sb.append(", TopicCode=");
        return C0284u0.c("Topic { ", gI.a(sb, this.f3598c, " }"));
    }
}
