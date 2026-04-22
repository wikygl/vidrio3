package C3;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class I<T> extends F3.u<T> {

    /* renamed from: m  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f429m = AtomicIntegerFieldUpdater.newUpdater(I.class, "_decision");
    private volatile int _decision;

    @Override // F3.u, C3.d0
    public final void e(Object obj) {
        m(obj);
    }

    @Override // F3.u, C3.d0
    public final void m(Object obj) {
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater;
        do {
            atomicIntegerFieldUpdater = f429m;
            int i4 = atomicIntegerFieldUpdater.get(this);
            if (i4 != 0) {
                if (i4 == 1) {
                    C.d(null);
                    throw null;
                }
                throw new IllegalStateException("Already resumed".toString());
            }
        } while (!atomicIntegerFieldUpdater.compareAndSet(this, 0, 2));
    }
}
