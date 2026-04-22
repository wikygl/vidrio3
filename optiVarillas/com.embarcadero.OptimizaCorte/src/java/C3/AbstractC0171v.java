package C3;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import n3.e;
import n3.f;

/* renamed from: C3.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class AbstractC0171v extends n3.a implements n3.e {

    /* renamed from: k  reason: collision with root package name */
    public static final a f502k = new n3.b(e.a.f5386j, C0170u.f501k);

    /* renamed from: C3.v$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a extends n3.b<n3.e, AbstractC0171v> {
    }

    public AbstractC0171v() {
        super(e.a.f5386j);
    }

    @Override // n3.a, n3.f
    public final <E extends f.b> E E(f.c<E> cVar) {
        v3.h.e(cVar, "key");
        if (cVar instanceof n3.b) {
            n3.b bVar = (n3.b) cVar;
            f.c<?> cVar2 = this.f5380j;
            v3.h.e(cVar2, "key");
            if (cVar2 != bVar && bVar.f5382k != cVar2) {
                return null;
            }
            E e4 = (E) bVar.a(this);
            if (!(e4 instanceof f.b)) {
                return null;
            }
            return e4;
        } else if (e.a.f5386j != cVar) {
            return null;
        } else {
            return this;
        }
    }

    public abstract void F(n3.f fVar, Runnable runnable);

    public boolean G() {
        return !(this instanceof n0);
    }

    @Override // n3.a, n3.f
    public final n3.f q(f.c<?> cVar) {
        v3.h.e(cVar, "key");
        boolean z4 = cVar instanceof n3.b;
        n3.g gVar = n3.g.f5388j;
        if (z4) {
            n3.b bVar = (n3.b) cVar;
            f.c<?> cVar2 = this.f5380j;
            v3.h.e(cVar2, "key");
            if ((cVar2 == bVar || bVar.f5382k == cVar2) && bVar.a(this) != null) {
                return gVar;
            }
        } else if (e.a.f5386j == cVar) {
            return gVar;
        }
        return this;
    }

    public String toString() {
        return getClass().getSimpleName() + '@' + C.c(this);
    }

    @Override // n3.e
    public final F3.h u(p3.c cVar) {
        return new F3.h(this, cVar);
    }

    @Override // n3.e
    public final void v(n3.d<?> dVar) {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater;
        C0155e c0155e;
        v3.h.c(dVar, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<*>");
        F3.h hVar = (F3.h) dVar;
        do {
            atomicReferenceFieldUpdater = F3.h.f915q;
        } while (atomicReferenceFieldUpdater.get(hVar) == F3.i.f921b);
        Object obj = atomicReferenceFieldUpdater.get(hVar);
        if (obj instanceof C0155e) {
            c0155e = (C0155e) obj;
        } else {
            c0155e = null;
        }
        if (c0155e != null) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater2 = C0155e.f469q;
            L l2 = (L) atomicReferenceFieldUpdater2.get(c0155e);
            if (l2 != null) {
                l2.d();
                atomicReferenceFieldUpdater2.set(c0155e, i0.f483j);
            }
        }
    }
}
