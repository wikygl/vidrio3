package h1;

import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class l extends r {

    /* renamed from: a  reason: collision with root package name */
    public final long f3567a;

    /* renamed from: b  reason: collision with root package name */
    public final long f3568b;

    /* renamed from: c  reason: collision with root package name */
    public final p f3569c;

    /* renamed from: d  reason: collision with root package name */
    public final Integer f3570d;

    /* renamed from: e  reason: collision with root package name */
    public final String f3571e;
    public final List<q> f;

    /* renamed from: g  reason: collision with root package name */
    public final u f3572g;

    public l() {
        throw null;
    }

    public l(long j4, long j5, j jVar, Integer num, String str, ArrayList arrayList) {
        u uVar = u.f3582j;
        this.f3567a = j4;
        this.f3568b = j5;
        this.f3569c = jVar;
        this.f3570d = num;
        this.f3571e = str;
        this.f = arrayList;
        this.f3572g = uVar;
    }

    @Override // h1.r
    public final p a() {
        return this.f3569c;
    }

    @Override // h1.r
    public final List<q> b() {
        return this.f;
    }

    @Override // h1.r
    public final Integer c() {
        return this.f3570d;
    }

    @Override // h1.r
    public final String d() {
        return this.f3571e;
    }

    @Override // h1.r
    public final u e() {
        return this.f3572g;
    }

    public final boolean equals(Object obj) {
        p pVar;
        Integer num;
        String str;
        List<q> list;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof r)) {
            return false;
        }
        r rVar = (r) obj;
        if (this.f3567a == rVar.f() && this.f3568b == rVar.g() && ((pVar = this.f3569c) != null ? pVar.equals(rVar.a()) : rVar.a() == null) && ((num = this.f3570d) != null ? num.equals(rVar.c()) : rVar.c() == null) && ((str = this.f3571e) != null ? str.equals(rVar.d()) : rVar.d() == null) && ((list = this.f) != null ? list.equals(rVar.b()) : rVar.b() == null)) {
            u uVar = this.f3572g;
            if (uVar == null) {
                if (rVar.e() == null) {
                    return true;
                }
            } else if (uVar.equals(rVar.e())) {
                return true;
            }
        }
        return false;
    }

    @Override // h1.r
    public final long f() {
        return this.f3567a;
    }

    @Override // h1.r
    public final long g() {
        return this.f3568b;
    }

    public final int hashCode() {
        int hashCode;
        int hashCode2;
        int hashCode3;
        int hashCode4;
        long j4 = this.f3567a;
        long j5 = this.f3568b;
        int i4 = (((((int) (j4 ^ (j4 >>> 32))) ^ 1000003) * 1000003) ^ ((int) ((j5 >>> 32) ^ j5))) * 1000003;
        int i5 = 0;
        p pVar = this.f3569c;
        if (pVar == null) {
            hashCode = 0;
        } else {
            hashCode = pVar.hashCode();
        }
        int i6 = (i4 ^ hashCode) * 1000003;
        Integer num = this.f3570d;
        if (num == null) {
            hashCode2 = 0;
        } else {
            hashCode2 = num.hashCode();
        }
        int i7 = (i6 ^ hashCode2) * 1000003;
        String str = this.f3571e;
        if (str == null) {
            hashCode3 = 0;
        } else {
            hashCode3 = str.hashCode();
        }
        int i8 = (i7 ^ hashCode3) * 1000003;
        List<q> list = this.f;
        if (list == null) {
            hashCode4 = 0;
        } else {
            hashCode4 = list.hashCode();
        }
        int i9 = (i8 ^ hashCode4) * 1000003;
        u uVar = this.f3572g;
        if (uVar != null) {
            i5 = uVar.hashCode();
        }
        return i9 ^ i5;
    }

    public final String toString() {
        return "LogRequest{requestTimeMs=" + this.f3567a + ", requestUptimeMs=" + this.f3568b + ", clientInfo=" + this.f3569c + ", logSource=" + this.f3570d + ", logSourceName=" + this.f3571e + ", logEvents=" + this.f + ", qosTier=" + this.f3572g + "}";
    }
}
