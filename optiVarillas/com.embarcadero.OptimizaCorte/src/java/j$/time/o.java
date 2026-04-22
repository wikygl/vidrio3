package j$.time;

import j$.time.chrono.AbstractC0483a;
import j$.time.chrono.AbstractC0491i;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class o implements j$.time.temporal.o, j$.time.temporal.p {
    public static final o APRIL;
    public static final o AUGUST;
    public static final o DECEMBER;
    public static final o FEBRUARY;
    public static final o JANUARY;
    public static final o JULY;
    public static final o JUNE;
    public static final o MARCH;
    public static final o MAY;
    public static final o NOVEMBER;
    public static final o OCTOBER;
    public static final o SEPTEMBER;

    /* renamed from: a  reason: collision with root package name */
    private static final o[] f3991a;

    /* renamed from: b  reason: collision with root package name */
    private static final /* synthetic */ o[] f3992b;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r13v1, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r14v1, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r15v1, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r3v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r4v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r5v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r6v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r7v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r8v2, types: [java.lang.Enum, j$.time.o] */
    /* JADX WARN: Type inference failed for: r9v2, types: [java.lang.Enum, j$.time.o] */
    static {
        ?? r12 = new Enum("JANUARY", 0);
        JANUARY = r12;
        ?? r13 = new Enum("FEBRUARY", 1);
        FEBRUARY = r13;
        ?? r14 = new Enum("MARCH", 2);
        MARCH = r14;
        ?? r15 = new Enum("APRIL", 3);
        APRIL = r15;
        ?? r9 = new Enum("MAY", 4);
        MAY = r9;
        ?? r8 = new Enum("JUNE", 5);
        JUNE = r8;
        ?? r7 = new Enum("JULY", 6);
        JULY = r7;
        ?? r6 = new Enum("AUGUST", 7);
        AUGUST = r6;
        ?? r5 = new Enum("SEPTEMBER", 8);
        SEPTEMBER = r5;
        ?? r4 = new Enum("OCTOBER", 9);
        OCTOBER = r4;
        ?? r32 = new Enum("NOVEMBER", 10);
        NOVEMBER = r32;
        ?? r22 = new Enum("DECEMBER", 11);
        DECEMBER = r22;
        f3992b = new o[]{r12, r13, r14, r15, r9, r8, r7, r6, r5, r4, r32, r22};
        f3991a = values();
    }

    public static o G(int i4) {
        if (i4 < 1 || i4 > 12) {
            throw new RuntimeException("Invalid value for MonthOfYear: " + i4);
        }
        return f3991a[i4 - 1];
    }

    public static o valueOf(String str) {
        return (o) Enum.valueOf(o.class, str);
    }

    public static o[] values() {
        return (o[]) f3992b.clone();
    }

    public final int D(boolean z4) {
        switch (n.f3990a[ordinal()]) {
            case 1:
                return 32;
            case 2:
                return (z4 ? 1 : 0) + 91;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return (z4 ? 1 : 0) + 152;
            case 4:
                return (z4 ? 1 : 0) + 244;
            case 5:
                return (z4 ? 1 : 0) + 305;
            case 6:
                return 1;
            case 7:
                return (z4 ? 1 : 0) + 60;
            case 8:
                return (z4 ? 1 : 0) + 121;
            case 9:
                return (z4 ? 1 : 0) + 182;
            case 10:
                return (z4 ? 1 : 0) + 213;
            case 11:
                return (z4 ? 1 : 0) + 274;
            default:
                return (z4 ? 1 : 0) + 335;
        }
    }

    public final int E(boolean z4) {
        int i4 = n.f3990a[ordinal()];
        return i4 != 1 ? (i4 == 2 || i4 == 3 || i4 == 4 || i4 == 5) ? 30 : 31 : z4 ? 29 : 28;
    }

    public final int F() {
        int i4 = n.f3990a[ordinal()];
        if (i4 != 1) {
            return (i4 == 2 || i4 == 3 || i4 == 4 || i4 == 5) ? 30 : 31;
        }
        return 29;
    }

    public final o H() {
        int i4 = ((int) 1) + 12;
        return f3991a[(i4 + ordinal()) % 12];
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.MONTH_OF_YEAR : rVar != null && rVar.m(this);
    }

    public final int getValue() {
        return ordinal() + 1;
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return rVar == j$.time.temporal.a.MONTH_OF_YEAR ? getValue() : j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return rVar == j$.time.temporal.a.MONTH_OF_YEAR ? rVar.j() : j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.MONTH_OF_YEAR) {
            return getValue();
        }
        if (rVar instanceof j$.time.temporal.a) {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.l(this);
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.e() ? j$.time.chrono.u.f3906d : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.MONTHS : j$.time.temporal.n.c(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        if (((AbstractC0483a) AbstractC0491i.p(mVar)).equals(j$.time.chrono.u.f3906d)) {
            return mVar.d(getValue(), j$.time.temporal.a.MONTH_OF_YEAR);
        }
        throw new RuntimeException("Adjustment only supported on ISO date-time");
    }
}
