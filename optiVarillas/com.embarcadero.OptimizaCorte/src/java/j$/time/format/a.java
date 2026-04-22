package j$.time.format;

import j$.time.A;
import j$.util.Objects;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a {
    public static final a f;

    /* renamed from: a  reason: collision with root package name */
    private final f f3919a;

    /* renamed from: b  reason: collision with root package name */
    private final Locale f3920b;

    /* renamed from: c  reason: collision with root package name */
    private final t f3921c;

    /* renamed from: d  reason: collision with root package name */
    private final j$.time.chrono.n f3922d;

    /* renamed from: e  reason: collision with root package name */
    private final A f3923e;

    static {
        o oVar = new o();
        j$.time.temporal.a aVar = j$.time.temporal.a.YEAR;
        v vVar = v.EXCEEDS_PAD;
        oVar.l(aVar, 4, 10, vVar);
        oVar.e('-');
        j$.time.temporal.a aVar2 = j$.time.temporal.a.MONTH_OF_YEAR;
        oVar.k(aVar2, 2);
        oVar.e('-');
        j$.time.temporal.a aVar3 = j$.time.temporal.a.DAY_OF_MONTH;
        oVar.k(aVar3, 2);
        u uVar = u.STRICT;
        j$.time.chrono.u uVar2 = j$.time.chrono.u.f3906d;
        a t3 = oVar.t(uVar, uVar2);
        o oVar2 = new o();
        oVar2.p();
        oVar2.a(t3);
        oVar2.h();
        oVar2.t(uVar, uVar2);
        o oVar3 = new o();
        oVar3.p();
        oVar3.a(t3);
        oVar3.o();
        oVar3.h();
        oVar3.t(uVar, uVar2);
        o oVar4 = new o();
        j$.time.temporal.a aVar4 = j$.time.temporal.a.HOUR_OF_DAY;
        oVar4.k(aVar4, 2);
        oVar4.e(':');
        j$.time.temporal.a aVar5 = j$.time.temporal.a.MINUTE_OF_HOUR;
        oVar4.k(aVar5, 2);
        oVar4.o();
        oVar4.e(':');
        j$.time.temporal.a aVar6 = j$.time.temporal.a.SECOND_OF_MINUTE;
        oVar4.k(aVar6, 2);
        oVar4.o();
        oVar4.b(j$.time.temporal.a.NANO_OF_SECOND);
        a t4 = oVar4.t(uVar, null);
        o oVar5 = new o();
        oVar5.p();
        oVar5.a(t4);
        oVar5.h();
        oVar5.t(uVar, null);
        o oVar6 = new o();
        oVar6.p();
        oVar6.a(t4);
        oVar6.o();
        oVar6.h();
        oVar6.t(uVar, null);
        o oVar7 = new o();
        oVar7.p();
        oVar7.a(t3);
        oVar7.e('T');
        oVar7.a(t4);
        a t5 = oVar7.t(uVar, uVar2);
        o oVar8 = new o();
        oVar8.p();
        oVar8.a(t5);
        oVar8.r();
        oVar8.h();
        oVar8.s();
        a t6 = oVar8.t(uVar, uVar2);
        o oVar9 = new o();
        oVar9.a(t6);
        oVar9.o();
        oVar9.e('[');
        oVar9.q();
        oVar9.m();
        oVar9.e(']');
        oVar9.t(uVar, uVar2);
        o oVar10 = new o();
        oVar10.a(t5);
        oVar10.o();
        oVar10.h();
        oVar10.o();
        oVar10.e('[');
        oVar10.q();
        oVar10.m();
        oVar10.e(']');
        oVar10.t(uVar, uVar2);
        o oVar11 = new o();
        oVar11.p();
        oVar11.l(aVar, 4, 10, vVar);
        oVar11.e('-');
        oVar11.k(j$.time.temporal.a.DAY_OF_YEAR, 3);
        oVar11.o();
        oVar11.h();
        oVar11.t(uVar, uVar2);
        o oVar12 = new o();
        oVar12.p();
        oVar12.l(j$.time.temporal.j.f4013c, 4, 10, vVar);
        oVar12.f("-W");
        oVar12.k(j$.time.temporal.j.f4012b, 2);
        oVar12.e('-');
        j$.time.temporal.a aVar7 = j$.time.temporal.a.DAY_OF_WEEK;
        oVar12.k(aVar7, 1);
        oVar12.o();
        oVar12.h();
        oVar12.t(uVar, uVar2);
        o oVar13 = new o();
        oVar13.p();
        oVar13.c();
        f = oVar13.t(uVar, null);
        o oVar14 = new o();
        oVar14.p();
        oVar14.k(aVar, 4);
        oVar14.k(aVar2, 2);
        oVar14.k(aVar3, 2);
        oVar14.o();
        oVar14.r();
        oVar14.g("+HHMMss", "Z");
        oVar14.s();
        oVar14.t(uVar, uVar2);
        HashMap hashMap = new HashMap();
        hashMap.put(1L, "Mon");
        hashMap.put(2L, "Tue");
        hashMap.put(3L, "Wed");
        hashMap.put(4L, "Thu");
        hashMap.put(5L, "Fri");
        hashMap.put(6L, "Sat");
        hashMap.put(7L, "Sun");
        HashMap hashMap2 = new HashMap();
        hashMap2.put(1L, "Jan");
        hashMap2.put(2L, "Feb");
        hashMap2.put(3L, "Mar");
        hashMap2.put(4L, "Apr");
        hashMap2.put(5L, "May");
        hashMap2.put(6L, "Jun");
        hashMap2.put(7L, "Jul");
        hashMap2.put(8L, "Aug");
        hashMap2.put(9L, "Sep");
        hashMap2.put(10L, "Oct");
        hashMap2.put(11L, "Nov");
        hashMap2.put(12L, "Dec");
        o oVar15 = new o();
        oVar15.p();
        oVar15.r();
        oVar15.o();
        oVar15.i(aVar7, hashMap);
        oVar15.f(", ");
        oVar15.n();
        oVar15.l(aVar3, 1, 2, v.NOT_NEGATIVE);
        oVar15.e(' ');
        oVar15.i(aVar2, hashMap2);
        oVar15.e(' ');
        oVar15.k(aVar, 4);
        oVar15.e(' ');
        oVar15.k(aVar4, 2);
        oVar15.e(':');
        oVar15.k(aVar5, 2);
        oVar15.o();
        oVar15.e(':');
        oVar15.k(aVar6, 2);
        oVar15.n();
        oVar15.e(' ');
        oVar15.g("+HHMM", "GMT");
        oVar15.t(u.SMART, uVar2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public a(f fVar, Locale locale, u uVar, j$.time.chrono.n nVar) {
        t tVar = t.f3962a;
        this.f3919a = (f) Objects.requireNonNull(fVar, "printerParser");
        this.f3920b = (Locale) Objects.requireNonNull(locale, "locale");
        this.f3921c = (t) Objects.requireNonNull(tVar, "decimalStyle");
        u uVar2 = (u) Objects.requireNonNull(uVar, "resolverStyle");
        this.f3922d = nVar;
        this.f3923e = null;
    }

    public final String a(j$.time.temporal.o oVar) {
        StringBuilder sb = new StringBuilder(32);
        Objects.requireNonNull(oVar, "temporal");
        Objects.requireNonNull(sb, "appendable");
        try {
            this.f3919a.j(new q(oVar, this), sb);
            return sb.toString();
        } catch (IOException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }

    public final j$.time.chrono.n b() {
        return this.f3922d;
    }

    public final t c() {
        return this.f3921c;
    }

    public final Locale d() {
        return this.f3920b;
    }

    public final A e() {
        return this.f3923e;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final f f() {
        return this.f3919a.a();
    }

    public final String toString() {
        String fVar = this.f3919a.toString();
        return fVar.startsWith("[") ? fVar : fVar.substring(1, fVar.length() - 1);
    }
}
