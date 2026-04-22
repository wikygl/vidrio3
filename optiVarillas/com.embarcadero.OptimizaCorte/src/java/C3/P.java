package C3;

import C3.O;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class P extends N {
    public abstract Thread K();

    /* JADX WARN: Type inference failed for: r5v0, types: [C3.O$b, F3.y, java.lang.Object] */
    public void L(long j4, O.a aVar) {
        boolean z4;
        int g4;
        Thread K3;
        D d4 = D.f426r;
        d4.getClass();
        if (O.f439q.get(d4) != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = O.f438p;
        O.a aVar2 = null;
        if (z4) {
            g4 = 1;
        } else {
            O.b bVar = (O.b) atomicReferenceFieldUpdater.get(d4);
            if (bVar == null) {
                ?? yVar = new F3.y();
                yVar.f442c = j4;
                while (!atomicReferenceFieldUpdater.compareAndSet(d4, null, yVar) && atomicReferenceFieldUpdater.get(d4) == null) {
                }
                Object obj = atomicReferenceFieldUpdater.get(d4);
                v3.h.b(obj);
                bVar = (O.b) obj;
            }
            g4 = aVar.g(j4, bVar, d4);
        }
        if (g4 != 0) {
            if (g4 != 1) {
                if (g4 != 2) {
                    throw new IllegalStateException("unexpected result".toString());
                }
                return;
            }
            d4.L(j4, aVar);
            throw null;
        }
        O.b bVar2 = (O.b) atomicReferenceFieldUpdater.get(d4);
        if (bVar2 != null) {
            aVar2 = bVar2.b();
        }
        if (aVar2 == aVar && Thread.currentThread() != (K3 = d4.K())) {
            LockSupport.unpark(K3);
        }
    }
}
