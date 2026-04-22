package h1;

import h1.q;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class k extends q {

    /* renamed from: a  reason: collision with root package name */
    public final long f3555a;

    /* renamed from: b  reason: collision with root package name */
    public final Integer f3556b;

    /* renamed from: c  reason: collision with root package name */
    public final long f3557c;

    /* renamed from: d  reason: collision with root package name */
    public final byte[] f3558d;

    /* renamed from: e  reason: collision with root package name */
    public final String f3559e;
    public final long f;

    /* renamed from: g  reason: collision with root package name */
    public final t f3560g;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a extends q.a {

        /* renamed from: a  reason: collision with root package name */
        public Long f3561a;

        /* renamed from: b  reason: collision with root package name */
        public Integer f3562b;

        /* renamed from: c  reason: collision with root package name */
        public Long f3563c;

        /* renamed from: d  reason: collision with root package name */
        public byte[] f3564d;

        /* renamed from: e  reason: collision with root package name */
        public String f3565e;
        public Long f;

        /* renamed from: g  reason: collision with root package name */
        public t f3566g;
    }

    public k(long j4, Integer num, long j5, byte[] bArr, String str, long j6, n nVar) {
        this.f3555a = j4;
        this.f3556b = num;
        this.f3557c = j5;
        this.f3558d = bArr;
        this.f3559e = str;
        this.f = j6;
        this.f3560g = nVar;
    }

    @Override // h1.q
    public final Integer a() {
        return this.f3556b;
    }

    @Override // h1.q
    public final long b() {
        return this.f3555a;
    }

    @Override // h1.q
    public final long c() {
        return this.f3557c;
    }

    @Override // h1.q
    public final t d() {
        return this.f3560g;
    }

    @Override // h1.q
    public final byte[] e() {
        return this.f3558d;
    }

    public final boolean equals(Object obj) {
        Integer num;
        byte[] e4;
        String str;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof q)) {
            return false;
        }
        q qVar = (q) obj;
        if (this.f3555a == qVar.b() && ((num = this.f3556b) != null ? num.equals(qVar.a()) : qVar.a() == null) && this.f3557c == qVar.c()) {
            if (qVar instanceof k) {
                e4 = ((k) qVar).f3558d;
            } else {
                e4 = qVar.e();
            }
            if (Arrays.equals(this.f3558d, e4) && ((str = this.f3559e) != null ? str.equals(qVar.f()) : qVar.f() == null) && this.f == qVar.g()) {
                t tVar = this.f3560g;
                if (tVar == null) {
                    if (qVar.d() == null) {
                        return true;
                    }
                } else if (tVar.equals(qVar.d())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // h1.q
    public final String f() {
        return this.f3559e;
    }

    @Override // h1.q
    public final long g() {
        return this.f;
    }

    public final int hashCode() {
        int hashCode;
        int hashCode2;
        long j4 = this.f3555a;
        int i4 = (((int) (j4 ^ (j4 >>> 32))) ^ 1000003) * 1000003;
        int i5 = 0;
        Integer num = this.f3556b;
        if (num == null) {
            hashCode = 0;
        } else {
            hashCode = num.hashCode();
        }
        long j5 = this.f3557c;
        int hashCode3 = (((((i4 ^ hashCode) * 1000003) ^ ((int) (j5 ^ (j5 >>> 32)))) * 1000003) ^ Arrays.hashCode(this.f3558d)) * 1000003;
        String str = this.f3559e;
        if (str == null) {
            hashCode2 = 0;
        } else {
            hashCode2 = str.hashCode();
        }
        long j6 = this.f;
        int i6 = (((hashCode3 ^ hashCode2) * 1000003) ^ ((int) (j6 ^ (j6 >>> 32)))) * 1000003;
        t tVar = this.f3560g;
        if (tVar != null) {
            i5 = tVar.hashCode();
        }
        return i6 ^ i5;
    }

    public final String toString() {
        return "LogEvent{eventTimeMs=" + this.f3555a + ", eventCode=" + this.f3556b + ", eventUptimeMs=" + this.f3557c + ", sourceExtension=" + Arrays.toString(this.f3558d) + ", sourceExtensionJsonProto3=" + this.f3559e + ", timezoneOffsetSeconds=" + this.f + ", networkConnectionInfo=" + this.f3560g + "}";
    }
}
