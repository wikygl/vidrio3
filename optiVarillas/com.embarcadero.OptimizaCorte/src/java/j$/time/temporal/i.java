package j$.time.temporal;

import j$.time.Duration;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
enum i implements u {
    WEEK_BASED_YEARS("WeekBasedYears"),
    QUARTER_YEARS("QuarterYears");
    

    /* renamed from: a  reason: collision with root package name */
    private final String f4010a;

    static {
        Duration duration = Duration.f3846c;
    }

    i(String str) {
        this.f4010a = str;
    }

    @Override // j$.time.temporal.u
    public final m j(m mVar, long j4) {
        int i4 = c.f4006a[ordinal()];
        if (i4 == 1) {
            r rVar = j.f4013c;
            return mVar.d(j$.com.android.tools.r8.a.i(mVar.j(rVar), j4), rVar);
        } else if (i4 == 2) {
            return mVar.e(j4 / 4, b.YEARS).e((j4 % 4) * 3, b.MONTHS);
        } else {
            throw new IllegalStateException("Unreachable");
        }
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f4010a;
    }
}
