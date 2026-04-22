package j$.time.format;

import j$.time.AbstractC0495d;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class o {
    private static final b f = new Object();

    /* renamed from: a  reason: collision with root package name */
    private o f3949a;

    /* renamed from: b  reason: collision with root package name */
    private final o f3950b;

    /* renamed from: c  reason: collision with root package name */
    private final ArrayList f3951c;

    /* renamed from: d  reason: collision with root package name */
    private final boolean f3952d;

    /* renamed from: e  reason: collision with root package name */
    private int f3953e;

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.time.format.b, java.lang.Object] */
    static {
        HashMap hashMap = new HashMap();
        hashMap.put('G', j$.time.temporal.a.ERA);
        hashMap.put('y', j$.time.temporal.a.YEAR_OF_ERA);
        hashMap.put('u', j$.time.temporal.a.YEAR);
        j$.time.temporal.r rVar = j$.time.temporal.j.f4011a;
        hashMap.put('Q', rVar);
        hashMap.put('q', rVar);
        j$.time.temporal.a aVar = j$.time.temporal.a.MONTH_OF_YEAR;
        hashMap.put('M', aVar);
        hashMap.put('L', aVar);
        hashMap.put('D', j$.time.temporal.a.DAY_OF_YEAR);
        hashMap.put('d', j$.time.temporal.a.DAY_OF_MONTH);
        hashMap.put('F', j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        j$.time.temporal.a aVar2 = j$.time.temporal.a.DAY_OF_WEEK;
        hashMap.put('E', aVar2);
        hashMap.put('c', aVar2);
        hashMap.put('e', aVar2);
        hashMap.put('a', j$.time.temporal.a.AMPM_OF_DAY);
        hashMap.put('H', j$.time.temporal.a.HOUR_OF_DAY);
        hashMap.put('k', j$.time.temporal.a.CLOCK_HOUR_OF_DAY);
        hashMap.put('K', j$.time.temporal.a.HOUR_OF_AMPM);
        hashMap.put('h', j$.time.temporal.a.CLOCK_HOUR_OF_AMPM);
        hashMap.put('m', j$.time.temporal.a.MINUTE_OF_HOUR);
        hashMap.put('s', j$.time.temporal.a.SECOND_OF_MINUTE);
        j$.time.temporal.a aVar3 = j$.time.temporal.a.NANO_OF_SECOND;
        hashMap.put('S', aVar3);
        hashMap.put('A', j$.time.temporal.a.MILLI_OF_DAY);
        hashMap.put('n', aVar3);
        hashMap.put('N', j$.time.temporal.a.NANO_OF_DAY);
        hashMap.put('g', j$.time.temporal.l.f4018a);
    }

    public o() {
        this.f3949a = this;
        this.f3951c = new ArrayList();
        this.f3953e = -1;
        this.f3950b = null;
        this.f3952d = false;
    }

    private o(o oVar) {
        this.f3949a = this;
        this.f3951c = new ArrayList();
        this.f3953e = -1;
        this.f3950b = oVar;
        this.f3952d = true;
    }

    private int d(g gVar) {
        Objects.requireNonNull(gVar, "pp");
        o oVar = this.f3949a;
        oVar.getClass();
        oVar.f3951c.add(gVar);
        o oVar2 = this.f3949a;
        oVar2.f3953e = -1;
        return oVar2.f3951c.size() - 1;
    }

    private void j(j jVar) {
        j b4;
        v vVar;
        o oVar = this.f3949a;
        int i4 = oVar.f3953e;
        if (i4 < 0) {
            oVar.f3953e = d(jVar);
            return;
        }
        j jVar2 = (j) oVar.f3951c.get(i4);
        int i5 = jVar.f3933b;
        int i6 = jVar.f3934c;
        if (i5 == i6) {
            vVar = jVar.f3935d;
            if (vVar == v.NOT_NEGATIVE) {
                b4 = jVar2.c(i6);
                d(jVar.b());
                this.f3949a.f3953e = i4;
                this.f3949a.f3951c.set(i4, b4);
            }
        }
        b4 = jVar2.b();
        this.f3949a.f3953e = d(jVar);
        this.f3949a.f3951c.set(i4, b4);
    }

    private a u(Locale locale, u uVar, j$.time.chrono.n nVar) {
        Objects.requireNonNull(locale, "locale");
        while (this.f3949a.f3950b != null) {
            n();
        }
        f fVar = new f((List) this.f3951c, false);
        t tVar = t.f3962a;
        return new a(fVar, locale, uVar, nVar);
    }

    public final void a(a aVar) {
        Objects.requireNonNull(aVar, "formatter");
        d(aVar.f());
    }

    public final void b(j$.time.temporal.r rVar) {
        h hVar = new h(rVar, 0, 9, true, 0);
        Objects.requireNonNull(rVar, "field");
        if (!rVar.j().g()) {
            throw new IllegalArgumentException(AbstractC0495d.a("Field must have a fixed set of values: ", rVar));
        }
        d(hVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.time.format.g, java.lang.Object] */
    public final void c() {
        d(new Object());
    }

    public final void e(char c4) {
        d(new e(c4));
    }

    public final void f(String str) {
        Objects.requireNonNull(str, "literal");
        if (str.isEmpty()) {
            return;
        }
        d(str.length() == 1 ? new e(str.charAt(0)) : new m(str, 0));
    }

    public final void g(String str, String str2) {
        d(new k(str, str2));
    }

    public final void h() {
        d(k.f3938e);
    }

    public final void i(j$.time.temporal.r rVar, HashMap hashMap) {
        Objects.requireNonNull(rVar, "field");
        Objects.requireNonNull(hashMap, "textLookup");
        LinkedHashMap linkedHashMap = new LinkedHashMap(hashMap);
        w wVar = w.FULL;
        d(new n(rVar, wVar, new c(new s(Collections.singletonMap(wVar, linkedHashMap)))));
    }

    public final void k(j$.time.temporal.r rVar, int i4) {
        Objects.requireNonNull(rVar, "field");
        if (i4 >= 1 && i4 <= 19) {
            j(new j(rVar, i4, i4, v.NOT_NEGATIVE));
            return;
        }
        throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + i4);
    }

    public final void l(j$.time.temporal.r rVar, int i4, int i5, v vVar) {
        if (i4 == i5 && vVar == v.NOT_NEGATIVE) {
            k(rVar, i5);
            return;
        }
        Objects.requireNonNull(rVar, "field");
        Objects.requireNonNull(vVar, "signStyle");
        if (i4 < 1 || i4 > 19) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + i4);
        } else if (i5 < 1 || i5 > 19) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + i5);
        } else if (i5 >= i4) {
            j(new j(rVar, i4, i5, vVar));
        } else {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + i5 + " < " + i4);
        }
    }

    public final void m() {
        d(new m(f, 1));
    }

    public final void n() {
        o oVar = this.f3949a;
        if (oVar.f3950b == null) {
            throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
        }
        if (oVar.f3951c.size() <= 0) {
            this.f3949a = this.f3949a.f3950b;
            return;
        }
        o oVar2 = this.f3949a;
        f fVar = new f(oVar2.f3951c, oVar2.f3952d);
        this.f3949a = this.f3949a.f3950b;
        d(fVar);
    }

    public final void o() {
        o oVar = this.f3949a;
        oVar.f3953e = -1;
        this.f3949a = new o(oVar);
    }

    public final void p() {
        d(l.INSENSITIVE);
    }

    public final void q() {
        d(l.SENSITIVE);
    }

    public final void r() {
        d(l.LENIENT);
    }

    public final void s() {
        d(l.STRICT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final a t(u uVar, j$.time.chrono.n nVar) {
        return u(Locale.getDefault(), uVar, nVar);
    }

    public final void v() {
        u(Locale.getDefault(), u.SMART, null);
    }
}
