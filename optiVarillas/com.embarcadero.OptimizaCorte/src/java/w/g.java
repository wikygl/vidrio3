package w;

import java.util.Iterator;
import w.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class g extends f {

    /* renamed from: m  reason: collision with root package name */
    public int f6358m;

    public g(m mVar) {
        super(mVar);
        if (mVar instanceof j) {
            this.f6342e = f.a.f6350k;
        } else {
            this.f6342e = f.a.f6351l;
        }
    }

    @Override // w.f
    public final void d(int i4) {
        if (this.f6346j) {
            return;
        }
        this.f6346j = true;
        this.f6343g = i4;
        Iterator it = this.f6347k.iterator();
        while (it.hasNext()) {
            d dVar = (d) it.next();
            dVar.a(dVar);
        }
    }
}
