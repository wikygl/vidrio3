package j$.time.format;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class n implements g {

    /* renamed from: a  reason: collision with root package name */
    private final j$.time.temporal.r f3945a;

    /* renamed from: b  reason: collision with root package name */
    private final w f3946b;

    /* renamed from: c  reason: collision with root package name */
    private final c f3947c;

    /* renamed from: d  reason: collision with root package name */
    private volatile j f3948d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public n(j$.time.temporal.r rVar, w wVar, c cVar) {
        this.f3945a = rVar;
        this.f3946b = wVar;
        this.f3947c = cVar;
    }

    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        Long e4 = qVar.e(this.f3945a);
        if (e4 == null) {
            return false;
        }
        j$.time.chrono.n nVar = (j$.time.chrono.n) qVar.d().u(j$.time.temporal.n.e());
        String c4 = (nVar == null || nVar == j$.time.chrono.u.f3906d) ? this.f3947c.c(this.f3945a, e4.longValue(), this.f3946b, qVar.c()) : this.f3947c.b(nVar, this.f3945a, e4.longValue(), this.f3946b, qVar.c());
        if (c4 != null) {
            sb.append(c4);
            return true;
        }
        if (this.f3948d == null) {
            this.f3948d = new j(this.f3945a, 1, 19, v.NORMAL);
        }
        return this.f3948d.j(qVar, sb);
    }

    public final String toString() {
        StringBuilder sb;
        w wVar = w.FULL;
        j$.time.temporal.r rVar = this.f3945a;
        w wVar2 = this.f3946b;
        if (wVar2 == wVar) {
            sb = new StringBuilder("Text(");
            sb.append(rVar);
        } else {
            sb = new StringBuilder("Text(");
            sb.append(rVar);
            sb.append(",");
            sb.append(wVar2);
        }
        sb.append(")");
        return sb.toString();
    }
}
