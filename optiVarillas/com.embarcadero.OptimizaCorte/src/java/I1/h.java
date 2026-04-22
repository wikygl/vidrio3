package i1;

import i1.n;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class h extends n {

    /* renamed from: a  reason: collision with root package name */
    public final String f3625a;

    /* renamed from: b  reason: collision with root package name */
    public final Integer f3626b;

    /* renamed from: c  reason: collision with root package name */
    public final m f3627c;

    /* renamed from: d  reason: collision with root package name */
    public final long f3628d;

    /* renamed from: e  reason: collision with root package name */
    public final long f3629e;
    public final Map<String, String> f;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a extends n.a {

        /* renamed from: a  reason: collision with root package name */
        public String f3630a;

        /* renamed from: b  reason: collision with root package name */
        public Integer f3631b;

        /* renamed from: c  reason: collision with root package name */
        public m f3632c;

        /* renamed from: d  reason: collision with root package name */
        public Long f3633d;

        /* renamed from: e  reason: collision with root package name */
        public Long f3634e;
        public Map<String, String> f;

        public final h b() {
            String str;
            if (this.f3630a == null) {
                str = " transportName";
            } else {
                str = "";
            }
            if (this.f3632c == null) {
                str = str.concat(" encodedPayload");
            }
            if (this.f3633d == null) {
                str = I.h.c(str, " eventMillis");
            }
            if (this.f3634e == null) {
                str = I.h.c(str, " uptimeMillis");
            }
            if (this.f == null) {
                str = I.h.c(str, " autoMetadata");
            }
            if (str.isEmpty()) {
                return new h(this.f3630a, this.f3631b, this.f3632c, this.f3633d.longValue(), this.f3634e.longValue(), this.f);
            }
            throw new IllegalStateException("Missing required properties:".concat(str));
        }
    }

    public h(String str, Integer num, m mVar, long j4, long j5, Map map) {
        this.f3625a = str;
        this.f3626b = num;
        this.f3627c = mVar;
        this.f3628d = j4;
        this.f3629e = j5;
        this.f = map;
    }

    @Override // i1.n
    public final Map<String, String> b() {
        return this.f;
    }

    @Override // i1.n
    public final Integer c() {
        return this.f3626b;
    }

    @Override // i1.n
    public final m d() {
        return this.f3627c;
    }

    @Override // i1.n
    public final long e() {
        return this.f3628d;
    }

    public final boolean equals(Object obj) {
        Integer num;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof n)) {
            return false;
        }
        n nVar = (n) obj;
        if (this.f3625a.equals(nVar.g()) && ((num = this.f3626b) != null ? num.equals(nVar.c()) : nVar.c() == null) && this.f3627c.equals(nVar.d()) && this.f3628d == nVar.e() && this.f3629e == nVar.h() && this.f.equals(nVar.b())) {
            return true;
        }
        return false;
    }

    @Override // i1.n
    public final String g() {
        return this.f3625a;
    }

    @Override // i1.n
    public final long h() {
        return this.f3629e;
    }

    public final int hashCode() {
        int hashCode;
        int hashCode2 = (this.f3625a.hashCode() ^ 1000003) * 1000003;
        Integer num = this.f3626b;
        if (num == null) {
            hashCode = 0;
        } else {
            hashCode = num.hashCode();
        }
        long j4 = this.f3628d;
        long j5 = this.f3629e;
        return ((((((((hashCode2 ^ hashCode) * 1000003) ^ this.f3627c.hashCode()) * 1000003) ^ ((int) (j4 ^ (j4 >>> 32)))) * 1000003) ^ ((int) (j5 ^ (j5 >>> 32)))) * 1000003) ^ this.f.hashCode();
    }

    public final String toString() {
        return "EventInternal{transportName=" + this.f3625a + ", code=" + this.f3626b + ", encodedPayload=" + this.f3627c + ", eventMillis=" + this.f3628d + ", uptimeMillis=" + this.f3629e + ", autoMetadata=" + this.f + "}";
    }
}
