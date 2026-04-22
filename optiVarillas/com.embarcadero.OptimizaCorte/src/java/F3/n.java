package F3;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class n<E> {

    /* renamed from: a  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f936a = AtomicReferenceFieldUpdater.newUpdater(n.class, Object.class, "_cur");
    private volatile Object _cur = new o(8, false);

    public final boolean a(E e4) {
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f936a;
            o oVar = (o) atomicReferenceFieldUpdater.get(this);
            int a4 = oVar.a(e4);
            if (a4 == 0) {
                return true;
            }
            if (a4 != 1) {
                if (a4 == 2) {
                    return false;
                }
            } else {
                o<E> c4 = oVar.c();
                while (!atomicReferenceFieldUpdater.compareAndSet(this, oVar, c4) && atomicReferenceFieldUpdater.get(this) == oVar) {
                }
            }
        }
    }

    public final void b() {
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f936a;
            o oVar = (o) atomicReferenceFieldUpdater.get(this);
            if (oVar.b()) {
                return;
            }
            o<E> c4 = oVar.c();
            while (!atomicReferenceFieldUpdater.compareAndSet(this, oVar, c4) && atomicReferenceFieldUpdater.get(this) == oVar) {
            }
        }
    }

    public final int c() {
        o oVar = (o) f936a.get(this);
        oVar.getClass();
        long j4 = o.f.get(oVar);
        return 1073741823 & (((int) ((j4 & 1152921503533105152L) >> 30)) - ((int) (1073741823 & j4)));
    }

    public final E d() {
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f936a;
            o oVar = (o) atomicReferenceFieldUpdater.get(this);
            E e4 = (E) oVar.d();
            if (e4 != o.f938g) {
                return e4;
            }
            o<E> c4 = oVar.c();
            while (!atomicReferenceFieldUpdater.compareAndSet(this, oVar, c4) && atomicReferenceFieldUpdater.get(this) == oVar) {
            }
        }
    }
}
