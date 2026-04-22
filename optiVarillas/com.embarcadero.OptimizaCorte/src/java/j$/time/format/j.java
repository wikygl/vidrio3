package j$.time.format;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class j implements g {
    static final long[] f = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L};

    /* renamed from: a  reason: collision with root package name */
    final j$.time.temporal.r f3932a;

    /* renamed from: b  reason: collision with root package name */
    final int f3933b;

    /* renamed from: c  reason: collision with root package name */
    final int f3934c;

    /* renamed from: d  reason: collision with root package name */
    private final v f3935d;

    /* renamed from: e  reason: collision with root package name */
    final int f3936e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public j(j$.time.temporal.r rVar, int i4, int i5, v vVar) {
        this.f3932a = rVar;
        this.f3933b = i4;
        this.f3934c = i5;
        this.f3935d = vVar;
        this.f3936e = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public j(j$.time.temporal.r rVar, int i4, int i5, v vVar, int i6) {
        this.f3932a = rVar;
        this.f3933b = i4;
        this.f3934c = i5;
        this.f3935d = vVar;
        this.f3936e = i6;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public j b() {
        if (this.f3936e == -1) {
            return this;
        }
        return new j(this.f3932a, this.f3933b, this.f3934c, this.f3935d, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public j c(int i4) {
        int i5 = this.f3934c;
        v vVar = this.f3935d;
        return new j(this.f3932a, this.f3933b, i5, vVar, this.f3936e + i4);
    }

    @Override // j$.time.format.g
    public boolean j(q qVar, StringBuilder sb) {
        j$.time.temporal.r rVar = this.f3932a;
        Long e4 = qVar.e(rVar);
        if (e4 == null) {
            return false;
        }
        long longValue = e4.longValue();
        t b4 = qVar.b();
        String l2 = longValue == Long.MIN_VALUE ? "9223372036854775808" : Long.toString(Math.abs(longValue));
        int length = l2.length();
        int i4 = this.f3934c;
        if (length > i4) {
            throw new RuntimeException("Field " + rVar + " cannot be printed as the value " + longValue + " exceeds the maximum print width of " + i4);
        }
        b4.getClass();
        int i5 = this.f3933b;
        v vVar = this.f3935d;
        if (longValue >= 0) {
            int i6 = d.f3927a[vVar.ordinal()];
            if (i6 == 1 ? !(i5 >= 19 || longValue < f[i5]) : i6 == 2) {
                sb.append('+');
            }
        } else {
            int i7 = d.f3927a[vVar.ordinal()];
            if (i7 == 1 || i7 == 2 || i7 == 3) {
                sb.append('-');
            } else if (i7 == 4) {
                throw new RuntimeException("Field " + rVar + " cannot be printed as the value " + longValue + " cannot be negative according to the SignStyle");
            }
        }
        for (int i8 = 0; i8 < i5 - l2.length(); i8++) {
            sb.append('0');
        }
        sb.append(l2);
        return true;
    }

    public String toString() {
        int i4 = this.f3934c;
        j$.time.temporal.r rVar = this.f3932a;
        v vVar = this.f3935d;
        int i5 = this.f3933b;
        if (i5 == 1 && i4 == 19 && vVar == v.NORMAL) {
            return "Value(" + rVar + ")";
        } else if (i5 == i4 && vVar == v.NOT_NEGATIVE) {
            return "Value(" + rVar + "," + i5 + ")";
        } else {
            return "Value(" + rVar + "," + i5 + "," + i4 + "," + vVar + ")";
        }
    }
}
