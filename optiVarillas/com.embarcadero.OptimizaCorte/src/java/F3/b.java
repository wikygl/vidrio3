package F3;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class b<T> extends r {

    /* renamed from: a  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f911a = AtomicReferenceFieldUpdater.newUpdater(b.class, Object.class, "_consensus");
    private volatile Object _consensus = C0206a.f910a;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // F3.r
    public final Object a(Object obj) {
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f911a;
        Object obj2 = atomicReferenceFieldUpdater.get(this);
        C1.A a4 = C0206a.f910a;
        if (obj2 == a4) {
            C1.A c4 = c(obj);
            obj2 = atomicReferenceFieldUpdater.get(this);
            if (obj2 == a4) {
                while (true) {
                    if (atomicReferenceFieldUpdater.compareAndSet(this, a4, c4)) {
                        obj2 = c4;
                        break;
                    } else if (atomicReferenceFieldUpdater.get(this) != a4) {
                        obj2 = atomicReferenceFieldUpdater.get(this);
                        break;
                    }
                }
            }
        }
        b(obj, obj2);
        return obj2;
    }

    public abstract void b(T t3, Object obj);

    public abstract C1.A c(Object obj);
}
