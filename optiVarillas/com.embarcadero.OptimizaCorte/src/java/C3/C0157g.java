package C3;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* renamed from: C3.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0157g extends a0 {

    /* renamed from: n  reason: collision with root package name */
    public final C0155e<?> f481n;

    public C0157g(C0155e<?> c0155e) {
        this.f481n = c0155e;
    }

    @Override // u3.l
    public final /* bridge */ /* synthetic */ l3.g g(Throwable th) {
        o(th);
        return l3.g.f5271a;
    }

    @Override // C3.AbstractC0164n
    public final void o(Throwable th) {
        d0 p4 = p();
        C0155e<?> c0155e = this.f481n;
        c0155e.getClass();
        CancellationException t3 = p4.t();
        if (c0155e.o()) {
            n3.d<?> dVar = c0155e.f470m;
            v3.h.c(dVar, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<*>");
            F3.h hVar = (F3.h) dVar;
            loop0: while (true) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = F3.h.f915q;
                Object obj = atomicReferenceFieldUpdater.get(hVar);
                C1.A a4 = F3.i.f921b;
                if (v3.h.a(obj, a4)) {
                    while (!atomicReferenceFieldUpdater.compareAndSet(hVar, a4, t3)) {
                        if (atomicReferenceFieldUpdater.get(hVar) != a4) {
                            break;
                        }
                    }
                    return;
                } else if (!(obj instanceof Throwable)) {
                    while (!atomicReferenceFieldUpdater.compareAndSet(hVar, obj, null)) {
                        if (atomicReferenceFieldUpdater.get(hVar) != obj) {
                            break;
                        }
                    }
                    break loop0;
                } else {
                    return;
                }
            }
        }
        c0155e.i(t3);
        if (!c0155e.o()) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = C0155e.f469q;
            L l2 = (L) atomicReferenceFieldUpdater2.get(c0155e);
            if (l2 != null) {
                l2.d();
                atomicReferenceFieldUpdater2.set(c0155e, i0.f483j);
            }
        }
    }
}
