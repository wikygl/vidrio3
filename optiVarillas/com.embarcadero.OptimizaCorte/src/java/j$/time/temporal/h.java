package j$.time.temporal;

import j$.time.EnumC0496e;
import j$.time.chrono.AbstractC0483a;
import j$.time.chrono.AbstractC0491i;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class h implements r {
    public static final h DAY_OF_QUARTER;
    public static final h QUARTER_OF_YEAR;
    public static final h WEEK_BASED_YEAR;
    public static final h WEEK_OF_WEEK_BASED_YEAR;

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f4007a;

    /* renamed from: b  reason: collision with root package name */
    private static final /* synthetic */ h[] f4008b;

    static {
        h hVar = new h() { // from class: j$.time.temporal.d
            @Override // j$.time.temporal.r
            public final w j() {
                return w.k(90L, 92L);
            }

            @Override // j$.time.temporal.r
            public final long l(o oVar) {
                int[] iArr;
                if (m(oVar)) {
                    int j4 = oVar.j(a.DAY_OF_YEAR);
                    int j5 = oVar.j(a.MONTH_OF_YEAR);
                    long r4 = oVar.r(a.YEAR);
                    iArr = h.f4007a;
                    int i4 = (j5 - 1) / 3;
                    j$.time.chrono.u.f3906d.getClass();
                    return j4 - iArr[i4 + (j$.time.chrono.u.m(r4) ? 4 : 0)];
                }
                throw new RuntimeException("Unsupported field: DayOfQuarter");
            }

            @Override // j$.time.temporal.r
            public final boolean m(o oVar) {
                if (oVar.f(a.DAY_OF_YEAR) && oVar.f(a.MONTH_OF_YEAR) && oVar.f(a.YEAR)) {
                    r rVar = j.f4011a;
                    if (((AbstractC0483a) AbstractC0491i.p(oVar)).equals(j$.time.chrono.u.f3906d)) {
                        return true;
                    }
                }
                return false;
            }

            @Override // j$.time.temporal.r
            public final m r(m mVar, long j4) {
                long l2 = l(mVar);
                j().b(j4, this);
                a aVar = a.DAY_OF_YEAR;
                return mVar.d((j4 - l2) + mVar.r(aVar), aVar);
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "DayOfQuarter";
            }

            @Override // j$.time.temporal.r
            public final w u(o oVar) {
                if (m(oVar)) {
                    long r4 = oVar.r(h.QUARTER_OF_YEAR);
                    if (r4 != 1) {
                        return r4 == 2 ? w.j(1L, 91L) : (r4 == 3 || r4 == 4) ? w.j(1L, 92L) : j();
                    }
                    long r5 = oVar.r(a.YEAR);
                    j$.time.chrono.u.f3906d.getClass();
                    return j$.time.chrono.u.m(r5) ? w.j(1L, 91L) : w.j(1L, 90L);
                }
                throw new RuntimeException("Unsupported field: DayOfQuarter");
            }
        };
        DAY_OF_QUARTER = hVar;
        h hVar2 = new h() { // from class: j$.time.temporal.e
            @Override // j$.time.temporal.r
            public final w j() {
                return w.j(1L, 4L);
            }

            @Override // j$.time.temporal.r
            public final long l(o oVar) {
                if (m(oVar)) {
                    return (oVar.r(a.MONTH_OF_YEAR) + 2) / 3;
                }
                throw new RuntimeException("Unsupported field: QuarterOfYear");
            }

            @Override // j$.time.temporal.r
            public final boolean m(o oVar) {
                if (oVar.f(a.MONTH_OF_YEAR)) {
                    r rVar = j.f4011a;
                    if (((AbstractC0483a) AbstractC0491i.p(oVar)).equals(j$.time.chrono.u.f3906d)) {
                        return true;
                    }
                }
                return false;
            }

            @Override // j$.time.temporal.r
            public final m r(m mVar, long j4) {
                long l2 = l(mVar);
                j().b(j4, this);
                a aVar = a.MONTH_OF_YEAR;
                return mVar.d(((j4 - l2) * 3) + mVar.r(aVar), aVar);
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "QuarterOfYear";
            }

            @Override // j$.time.temporal.r
            public final w u(o oVar) {
                if (m(oVar)) {
                    return j();
                }
                throw new RuntimeException("Unsupported field: QuarterOfYear");
            }
        };
        QUARTER_OF_YEAR = hVar2;
        h hVar3 = new h() { // from class: j$.time.temporal.f
            @Override // j$.time.temporal.r
            public final w j() {
                return w.k(52L, 53L);
            }

            @Override // j$.time.temporal.r
            public final long l(o oVar) {
                if (m(oVar)) {
                    return h.D(j$.time.i.F(oVar));
                }
                throw new RuntimeException("Unsupported field: WeekOfWeekBasedYear");
            }

            @Override // j$.time.temporal.r
            public final boolean m(o oVar) {
                if (oVar.f(a.EPOCH_DAY)) {
                    r rVar = j.f4011a;
                    if (((AbstractC0483a) AbstractC0491i.p(oVar)).equals(j$.time.chrono.u.f3906d)) {
                        return true;
                    }
                }
                return false;
            }

            @Override // j$.time.temporal.r
            public final m r(m mVar, long j4) {
                j().b(j4, this);
                return mVar.e(j$.com.android.tools.r8.a.p(j4, l(mVar)), b.WEEKS);
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "WeekOfWeekBasedYear";
            }

            @Override // j$.time.temporal.r
            public final w u(o oVar) {
                if (m(oVar)) {
                    return h.G(j$.time.i.F(oVar));
                }
                throw new RuntimeException("Unsupported field: WeekOfWeekBasedYear");
            }
        };
        WEEK_OF_WEEK_BASED_YEAR = hVar3;
        h hVar4 = new h() { // from class: j$.time.temporal.g
            @Override // j$.time.temporal.r
            public final w j() {
                return a.YEAR.j();
            }

            @Override // j$.time.temporal.r
            public final long l(o oVar) {
                int H4;
                if (m(oVar)) {
                    H4 = h.H(j$.time.i.F(oVar));
                    return H4;
                }
                throw new RuntimeException("Unsupported field: WeekBasedYear");
            }

            @Override // j$.time.temporal.r
            public final boolean m(o oVar) {
                if (oVar.f(a.EPOCH_DAY)) {
                    r rVar = j.f4011a;
                    if (((AbstractC0483a) AbstractC0491i.p(oVar)).equals(j$.time.chrono.u.f3906d)) {
                        return true;
                    }
                }
                return false;
            }

            @Override // j$.time.temporal.r
            public final m r(m mVar, long j4) {
                int I2;
                if (m(mVar)) {
                    int a4 = a.YEAR.j().a(j4, h.WEEK_BASED_YEAR);
                    j$.time.i F4 = j$.time.i.F(mVar);
                    a aVar = a.DAY_OF_WEEK;
                    int j5 = F4.j(aVar);
                    int D4 = h.D(F4);
                    if (D4 == 53) {
                        I2 = h.I(a4);
                        if (I2 == 52) {
                            D4 = 52;
                        }
                    }
                    j$.time.i O3 = j$.time.i.O(a4, 1, 4);
                    return mVar.l(O3.S(((D4 - 1) * 7) + (j5 - O3.j(aVar))));
                }
                throw new RuntimeException("Unsupported field: WeekBasedYear");
            }

            @Override // java.lang.Enum
            public final String toString() {
                return "WeekBasedYear";
            }

            @Override // j$.time.temporal.r
            public final w u(o oVar) {
                if (m(oVar)) {
                    return a.YEAR.j();
                }
                throw new RuntimeException("Unsupported field: WeekBasedYear");
            }
        };
        WEEK_BASED_YEAR = hVar4;
        f4008b = new h[]{hVar, hVar2, hVar3, hVar4};
        f4007a = new int[]{0, 90, 181, 273, 0, 91, 182, 274};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int D(j$.time.i iVar) {
        int ordinal = iVar.H().ordinal();
        int i4 = 1;
        int I2 = iVar.I() - 1;
        int i5 = (3 - ordinal) + I2;
        int i6 = i5 - ((i5 / 7) * 7);
        int i7 = i6 - 3;
        if (i7 < -3) {
            i7 = i6 + 4;
        }
        if (I2 < i7) {
            return (int) w.j(1L, I(H(iVar.Y(180).U(-1L)))).d();
        }
        int i8 = ((I2 - i7) / 7) + 1;
        if (i8 != 53 || i7 == -3 || (i7 == -2 && iVar.M())) {
            i4 = i8;
        }
        return i4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static w G(j$.time.i iVar) {
        return w.j(1L, I(H(iVar)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int H(j$.time.i iVar) {
        int K3 = iVar.K();
        int I2 = iVar.I();
        if (I2 <= 3) {
            return I2 - iVar.H().ordinal() < -2 ? K3 - 1 : K3;
        } else if (I2 >= 363) {
            return ((I2 - 363) - (iVar.M() ? 1 : 0)) - iVar.H().ordinal() >= 0 ? K3 + 1 : K3;
        } else {
            return K3;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int I(int i4) {
        j$.time.i O3 = j$.time.i.O(i4, 1, 1);
        if (O3.H() != EnumC0496e.THURSDAY) {
            return (O3.H() == EnumC0496e.WEDNESDAY && O3.M()) ? 53 : 52;
        }
        return 53;
    }

    public static h valueOf(String str) {
        return (h) Enum.valueOf(h.class, str);
    }

    public static h[] values() {
        return (h[]) f4008b.clone();
    }

    @Override // j$.time.temporal.r
    public final boolean v() {
        return true;
    }
}
