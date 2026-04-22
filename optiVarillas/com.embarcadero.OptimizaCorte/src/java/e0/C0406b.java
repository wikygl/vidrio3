package e0;

import C3.F;
import C3.G;
import java.util.concurrent.CancellationException;
import l3.g;
import s.AbstractC0788a;
import s.C0789b;
import s.C0791d;
import u3.l;
import v3.i;

/* renamed from: e0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0406b extends i implements l<Throwable, g> {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0789b<Object> f3353k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ F<Object> f3354l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0406b(C0789b c0789b, G g4) {
        super(1);
        this.f3353k = c0789b;
        this.f3354l = g4;
    }

    @Override // u3.l
    public final g g(Throwable th) {
        Throwable th2 = th;
        C0789b<Object> c0789b = this.f3353k;
        if (th2 != null) {
            if (th2 instanceof CancellationException) {
                c0789b.f5752d = true;
                C0791d<Object> c0791d = c0789b.f5750b;
                if (c0791d != null && c0791d.f5754k.cancel(true)) {
                    c0789b.f5749a = null;
                    c0789b.f5750b = null;
                    c0789b.f5751c = null;
                }
            } else {
                c0789b.f5752d = true;
                C0791d<Object> c0791d2 = c0789b.f5750b;
                if (c0791d2 != null && c0791d2.f5754k.i(th2)) {
                    c0789b.f5749a = null;
                    c0789b.f5750b = null;
                    c0789b.f5751c = null;
                }
            }
        } else {
            Object f = this.f3354l.f();
            c0789b.f5752d = true;
            C0791d<Object> c0791d3 = c0789b.f5750b;
            if (c0791d3 != null) {
                C0791d.a aVar = c0791d3.f5754k;
                aVar.getClass();
                if (f == null) {
                    f = AbstractC0788a.f5729p;
                }
                if (AbstractC0788a.f5728o.b(aVar, null, f)) {
                    AbstractC0788a.c(aVar);
                    c0789b.f5749a = null;
                    c0789b.f5750b = null;
                    c0789b.f5751c = null;
                }
            }
        }
        return g.f5271a;
    }
}
