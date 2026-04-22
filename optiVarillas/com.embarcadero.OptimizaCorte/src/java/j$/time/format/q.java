package j$.time.format;

import java.util.Locale;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class q {

    /* renamed from: a  reason: collision with root package name */
    private j$.time.temporal.o f3958a;

    /* renamed from: b  reason: collision with root package name */
    private a f3959b;

    /* renamed from: c  reason: collision with root package name */
    private int f3960c;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00b5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public q(j$.time.temporal.o r10, j$.time.format.a r11) {
        /*
            Method dump skipped, instructions count: 327
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.q.<init>(j$.time.temporal.o, j$.time.format.a):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a() {
        this.f3960c--;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final t b() {
        return this.f3959b.c();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Locale c() {
        return this.f3959b.d();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final j$.time.temporal.o d() {
        return this.f3958a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Long e(j$.time.temporal.r rVar) {
        int i4 = this.f3960c;
        j$.time.temporal.o oVar = this.f3958a;
        if (i4 <= 0 || oVar.f(rVar)) {
            return Long.valueOf(oVar.r(rVar));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object f(b bVar) {
        j$.time.temporal.o oVar = this.f3958a;
        Object u4 = oVar.u(bVar);
        if (u4 == null && this.f3960c == 0) {
            throw new RuntimeException("Unable to extract " + bVar + " from temporal " + oVar);
        }
        return u4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void g() {
        this.f3960c++;
    }

    public final String toString() {
        return this.f3958a.toString();
    }
}
