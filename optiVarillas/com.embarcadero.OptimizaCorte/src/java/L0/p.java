package L0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class p {

    /* renamed from: a  reason: collision with root package name */
    public String f1450a;

    /* renamed from: b  reason: collision with root package name */
    public C0.o f1451b = C0.o.f337j;

    /* renamed from: c  reason: collision with root package name */
    public String f1452c;

    /* renamed from: d  reason: collision with root package name */
    public String f1453d;

    /* renamed from: e  reason: collision with root package name */
    public androidx.work.b f1454e;
    public androidx.work.b f;

    /* renamed from: g  reason: collision with root package name */
    public long f1455g;

    /* renamed from: h  reason: collision with root package name */
    public long f1456h;

    /* renamed from: i  reason: collision with root package name */
    public long f1457i;

    /* renamed from: j  reason: collision with root package name */
    public C0.c f1458j;

    /* renamed from: k  reason: collision with root package name */
    public int f1459k;

    /* renamed from: l  reason: collision with root package name */
    public C0.a f1460l;

    /* renamed from: m  reason: collision with root package name */
    public long f1461m;

    /* renamed from: n  reason: collision with root package name */
    public long f1462n;

    /* renamed from: o  reason: collision with root package name */
    public long f1463o;

    /* renamed from: p  reason: collision with root package name */
    public long f1464p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f1465q;

    /* renamed from: r  reason: collision with root package name */
    public C0.m f1466r;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public String f1467a;

        /* renamed from: b  reason: collision with root package name */
        public C0.o f1468b;

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof a)) {
                return false;
            }
            a aVar = (a) obj;
            if (this.f1468b != aVar.f1468b) {
                return false;
            }
            return this.f1467a.equals(aVar.f1467a);
        }

        public final int hashCode() {
            return this.f1468b.hashCode() + (this.f1467a.hashCode() * 31);
        }
    }

    static {
        C0.i.e("WorkSpec");
    }

    public p(String str, String str2) {
        androidx.work.b bVar = androidx.work.b.c;
        this.f1454e = bVar;
        this.f = bVar;
        this.f1458j = C0.c.f303i;
        this.f1460l = C0.a.f298j;
        this.f1461m = 30000L;
        this.f1464p = -1L;
        this.f1466r = C0.m.f334j;
        this.f1450a = str;
        this.f1452c = str2;
    }

    public final long a() {
        int i4;
        long scalb;
        if (this.f1451b == C0.o.f337j && (i4 = this.f1459k) > 0) {
            if (this.f1460l == C0.a.f299k) {
                scalb = this.f1461m * i4;
            } else {
                scalb = Math.scalb((float) this.f1461m, i4 - 1);
            }
            return Math.min(18000000L, scalb) + this.f1462n;
        }
        long j4 = 0;
        if (c()) {
            long currentTimeMillis = System.currentTimeMillis();
            long j5 = this.f1462n;
            int i5 = (j5 > 0L ? 1 : (j5 == 0L ? 0 : -1));
            if (i5 == 0) {
                j5 = this.f1455g + currentTimeMillis;
            }
            long j6 = this.f1457i;
            long j7 = this.f1456h;
            if (j6 != j7) {
                if (i5 == 0) {
                    j4 = j6 * (-1);
                }
                return j5 + j7 + j4;
            }
            if (i5 != 0) {
                j4 = j7;
            }
            return j5 + j4;
        }
        long j8 = this.f1462n;
        if (j8 == 0) {
            j8 = System.currentTimeMillis();
        }
        return j8 + this.f1455g;
    }

    public final boolean b() {
        return !C0.c.f303i.equals(this.f1458j);
    }

    public final boolean c() {
        if (this.f1456h != 0) {
            return true;
        }
        return false;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || p.class != obj.getClass()) {
            return false;
        }
        p pVar = (p) obj;
        if (this.f1455g != pVar.f1455g || this.f1456h != pVar.f1456h || this.f1457i != pVar.f1457i || this.f1459k != pVar.f1459k || this.f1461m != pVar.f1461m || this.f1462n != pVar.f1462n || this.f1463o != pVar.f1463o || this.f1464p != pVar.f1464p || this.f1465q != pVar.f1465q || !this.f1450a.equals(pVar.f1450a) || this.f1451b != pVar.f1451b || !this.f1452c.equals(pVar.f1452c)) {
            return false;
        }
        String str = this.f1453d;
        if (str == null ? pVar.f1453d != null : !str.equals(pVar.f1453d)) {
            return false;
        }
        if (this.f1454e.equals(pVar.f1454e) && this.f.equals(pVar.f) && this.f1458j.equals(pVar.f1458j) && this.f1460l == pVar.f1460l && this.f1466r == pVar.f1466r) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int i4;
        int hashCode = this.f1451b.hashCode();
        int hashCode2 = (this.f1452c.hashCode() + ((hashCode + (this.f1450a.hashCode() * 31)) * 31)) * 31;
        String str = this.f1453d;
        if (str != null) {
            i4 = str.hashCode();
        } else {
            i4 = 0;
        }
        int hashCode3 = this.f1454e.hashCode();
        int hashCode4 = this.f.hashCode();
        long j4 = this.f1455g;
        long j5 = this.f1456h;
        long j6 = this.f1457i;
        int hashCode5 = this.f1458j.hashCode();
        int hashCode6 = this.f1460l.hashCode();
        long j7 = this.f1461m;
        long j8 = this.f1462n;
        long j9 = this.f1463o;
        long j10 = this.f1464p;
        return this.f1466r.hashCode() + ((((((((((((hashCode6 + ((((hashCode5 + ((((((((hashCode4 + ((hashCode3 + ((hashCode2 + i4) * 31)) * 31)) * 31) + ((int) (j4 ^ (j4 >>> 32)))) * 31) + ((int) (j5 ^ (j5 >>> 32)))) * 31) + ((int) (j6 ^ (j6 >>> 32)))) * 31)) * 31) + this.f1459k) * 31)) * 31) + ((int) (j7 ^ (j7 >>> 32)))) * 31) + ((int) (j8 ^ (j8 >>> 32)))) * 31) + ((int) (j9 ^ (j9 >>> 32)))) * 31) + ((int) (j10 ^ (j10 >>> 32)))) * 31) + (this.f1465q ? 1 : 0)) * 31);
    }

    public final String toString() {
        return C.b.c(new StringBuilder("{WorkSpec: "), this.f1450a, "}");
    }
}
