package D0;

import C0.l;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.p;
import m.C0723b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b implements C0.l {

    /* renamed from: c  reason: collision with root package name */
    public final p<l.a> f553c = new p<>();

    /* renamed from: d  reason: collision with root package name */
    public final N0.c<l.a.c> f554d = new N0.a();

    /* JADX WARN: Type inference failed for: r0v1, types: [N0.a, N0.c<C0.l$a$c>] */
    public b() {
        a(C0.l.f332b);
    }

    public final void a(l.a aVar) {
        boolean z4;
        p<l.a> pVar = this.f553c;
        synchronized (((LiveData) pVar).a) {
            if (((LiveData) pVar).f == LiveData.k) {
                z4 = true;
            } else {
                z4 = false;
            }
            ((LiveData) pVar).f = aVar;
        }
        if (z4) {
            C0723b.F().G(((LiveData) pVar).j);
        }
        if (aVar instanceof l.a.c) {
            this.f554d.j((l.a.c) aVar);
        } else if (aVar instanceof l.a.C0003a) {
            this.f554d.k(((l.a.C0003a) aVar).f333a);
        }
    }
}
