package j$.time.temporal;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
enum k implements r {
    JULIAN_DAY("JulianDay", 2440588),
    MODIFIED_JULIAN_DAY("ModifiedJulianDay", 40587),
    RATA_DIE("RataDie", 719163);
    
    private static final long serialVersionUID = -7501623920830201812L;

    /* renamed from: a  reason: collision with root package name */
    private final transient String f4015a;

    /* renamed from: b  reason: collision with root package name */
    private final transient w f4016b;

    /* renamed from: c  reason: collision with root package name */
    private final transient long f4017c;

    static {
        b bVar = b.NANOS;
    }

    k(String str, long j4) {
        this.f4015a = str;
        this.f4016b = w.j((-365243219162L) + j4, 365241780471L + j4);
        this.f4017c = j4;
    }

    @Override // j$.time.temporal.r
    public final w j() {
        return this.f4016b;
    }

    @Override // j$.time.temporal.r
    public final long l(o oVar) {
        return oVar.r(a.EPOCH_DAY) + this.f4017c;
    }

    @Override // j$.time.temporal.r
    public final boolean m(o oVar) {
        return oVar.f(a.EPOCH_DAY);
    }

    @Override // j$.time.temporal.r
    public final m r(m mVar, long j4) {
        if (this.f4016b.i(j4)) {
            return mVar.d(j$.com.android.tools.r8.a.p(j4, this.f4017c), a.EPOCH_DAY);
        }
        throw new RuntimeException("Invalid value: " + this.f4015a + " " + j4);
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f4015a;
    }

    @Override // j$.time.temporal.r
    public final w u(o oVar) {
        if (oVar.f(a.EPOCH_DAY)) {
            return this.f4016b;
        }
        throw new RuntimeException("Unsupported field: " + this);
    }

    @Override // j$.time.temporal.r
    public final boolean v() {
        return true;
    }
}
