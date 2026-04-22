package C3;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class c0 extends AbstractC0164n implements L, U {

    /* renamed from: m  reason: collision with root package name */
    public d0 f456m;

    @Override // C3.U
    public final boolean a() {
        return true;
    }

    @Override // C3.L
    public final void d() {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2;
        d0 p4 = p();
        while (true) {
            Object G4 = p4.G();
            if (G4 instanceof c0) {
                if (G4 == this) {
                    M m4 = f0.f480g;
                    do {
                        atomicReferenceFieldUpdater2 = d0.f457j;
                        if (atomicReferenceFieldUpdater2.compareAndSet(p4, G4, m4)) {
                            return;
                        }
                    } while (atomicReferenceFieldUpdater2.get(p4) == G4);
                } else {
                    return;
                }
            } else if (!(G4 instanceof U) || ((U) G4).h() == null) {
                return;
            } else {
                while (true) {
                    Object l2 = l();
                    if (l2 instanceof F3.s) {
                        F3.m mVar = ((F3.s) l2).f945a;
                        return;
                    } else if (l2 == this) {
                        F3.m mVar2 = (F3.m) l2;
                        return;
                    } else {
                        v3.h.c(l2, "null cannot be cast to non-null type kotlinx.coroutines.internal.LockFreeLinkedListNode{ kotlinx.coroutines.internal.LockFreeLinkedListKt.Node }");
                        F3.m mVar3 = (F3.m) l2;
                        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater3 = F3.m.f933l;
                        F3.s sVar = (F3.s) atomicReferenceFieldUpdater3.get(mVar3);
                        if (sVar == null) {
                            sVar = new F3.s(mVar3);
                            atomicReferenceFieldUpdater3.lazySet(mVar3, sVar);
                        }
                        do {
                            atomicReferenceFieldUpdater = F3.m.f931j;
                            if (atomicReferenceFieldUpdater.compareAndSet(this, l2, sVar)) {
                                mVar3.i();
                                return;
                            }
                        } while (atomicReferenceFieldUpdater.get(this) == l2);
                    }
                }
            }
        }
    }

    @Override // C3.U
    public final h0 h() {
        return null;
    }

    public final d0 p() {
        d0 d0Var = this.f456m;
        if (d0Var != null) {
            return d0Var;
        }
        RuntimeException runtimeException = new RuntimeException("lateinit property job has not been initialized");
        v3.h.f(runtimeException, v3.h.class.getName());
        throw runtimeException;
    }

    @Override // F3.m
    public final String toString() {
        return getClass().getSimpleName() + '@' + C.c(this) + "[job@" + C.c(p()) + ']';
    }
}
