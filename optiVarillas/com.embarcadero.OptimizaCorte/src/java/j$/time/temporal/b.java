package j$.time.temporal;

import j$.time.Duration;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public enum b implements u {
    NANOS("Nanos"),
    MICROS("Micros"),
    MILLIS("Millis"),
    SECONDS("Seconds"),
    MINUTES("Minutes"),
    HOURS("Hours"),
    HALF_DAYS("HalfDays"),
    DAYS("Days"),
    WEEKS("Weeks"),
    MONTHS("Months"),
    YEARS("Years"),
    DECADES("Decades"),
    CENTURIES("Centuries"),
    MILLENNIA("Millennia"),
    ERAS("Eras"),
    FOREVER("Forever");
    

    /* renamed from: a  reason: collision with root package name */
    private final String f4005a;

    static {
        Duration duration = Duration.f3846c;
        Duration.u(Long.MAX_VALUE, 999999999L);
    }

    b(String str) {
        this.f4005a = str;
    }

    @Override // j$.time.temporal.u
    public final m j(m mVar, long j4) {
        return mVar.e(j4, this);
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f4005a;
    }
}
