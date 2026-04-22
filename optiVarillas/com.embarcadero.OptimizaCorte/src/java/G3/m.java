package G3;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m {

    /* renamed from: b  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f1007b = AtomicReferenceFieldUpdater.newUpdater(m.class, Object.class, "lastScheduledTask");

    /* renamed from: c  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f1008c = AtomicIntegerFieldUpdater.newUpdater(m.class, "producerIndex");

    /* renamed from: d  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f1009d = AtomicIntegerFieldUpdater.newUpdater(m.class, "consumerIndex");

    /* renamed from: e  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f1010e = AtomicIntegerFieldUpdater.newUpdater(m.class, "blockingTasksInBuffer");

    /* renamed from: a  reason: collision with root package name */
    public final AtomicReferenceArray<h> f1011a = new AtomicReferenceArray<>(128);
    private volatile int blockingTasksInBuffer;
    private volatile int consumerIndex;
    private volatile Object lastScheduledTask;
    private volatile int producerIndex;

    public final h a(h hVar) {
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = f1008c;
        if (atomicIntegerFieldUpdater.get(this) - f1009d.get(this) == 127) {
            return hVar;
        }
        if (hVar.f996k.a() == 1) {
            f1010e.incrementAndGet(this);
        }
        int i4 = atomicIntegerFieldUpdater.get(this) & 127;
        while (true) {
            AtomicReferenceArray<h> atomicReferenceArray = this.f1011a;
            if (atomicReferenceArray.get(i4) != null) {
                Thread.yield();
            } else {
                atomicReferenceArray.lazySet(i4, hVar);
                atomicIntegerFieldUpdater.incrementAndGet(this);
                return null;
            }
        }
    }

    public final h b() {
        h andSet;
        while (true) {
            AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = f1009d;
            int i4 = atomicIntegerFieldUpdater.get(this);
            if (i4 - f1008c.get(this) == 0) {
                return null;
            }
            int i5 = i4 & 127;
            if (atomicIntegerFieldUpdater.compareAndSet(this, i4, i4 + 1) && (andSet = this.f1011a.getAndSet(i5, null)) != null) {
                if (andSet.f996k.a() == 1) {
                    f1010e.decrementAndGet(this);
                }
                return andSet;
            }
        }
    }

    public final h c(int i4, boolean z4) {
        int i5 = i4 & 127;
        AtomicReferenceArray<h> atomicReferenceArray = this.f1011a;
        h hVar = atomicReferenceArray.get(i5);
        if (hVar != null) {
            boolean z5 = true;
            if (hVar.f996k.a() != 1) {
                z5 = false;
            }
            if (z5 == z4) {
                while (!atomicReferenceArray.compareAndSet(i5, hVar, null)) {
                    if (atomicReferenceArray.get(i5) != hVar) {
                    }
                }
                if (z4) {
                    f1010e.decrementAndGet(this);
                }
                return hVar;
            }
        }
        return null;
    }
}
