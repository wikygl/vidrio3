package G3;

import C3.S;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class f extends S {

    /* renamed from: l  reason: collision with root package name */
    public final a f993l;

    public f(int i4, int i5, long j4, String str) {
        this.f993l = new a(i4, i5, j4, str);
    }

    @Override // C3.AbstractC0171v
    public final void F(n3.f fVar, Runnable runnable) {
        a aVar = this.f993l;
        AtomicLongFieldUpdater atomicLongFieldUpdater = a.f963q;
        aVar.b(runnable, k.f1004g, false);
    }
}
