package C3;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class O extends P implements H {

    /* renamed from: o  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f437o = AtomicReferenceFieldUpdater.newUpdater(O.class, Object.class, "_queue");

    /* renamed from: p  reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f438p = AtomicReferenceFieldUpdater.newUpdater(O.class, Object.class, "_delayed");

    /* renamed from: q  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f439q = AtomicIntegerFieldUpdater.newUpdater(O.class, "_isCompleted");
    private volatile Object _delayed;
    private volatile int _isCompleted = 0;
    private volatile Object _queue;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class a implements Runnable, Comparable<a>, L, F3.z {
        private volatile Object _heap;

        /* renamed from: j  reason: collision with root package name */
        public long f440j;

        /* renamed from: k  reason: collision with root package name */
        public int f441k;

        @Override // F3.z
        public final void a(int i4) {
            this.f441k = i4;
        }

        @Override // F3.z
        public final int c() {
            return this.f441k;
        }

        @Override // java.lang.Comparable
        public final int compareTo(a aVar) {
            int i4 = ((this.f440j - aVar.f440j) > 0L ? 1 : ((this.f440j - aVar.f440j) == 0L ? 0 : -1));
            if (i4 > 0) {
                return 1;
            }
            if (i4 < 0) {
                return -1;
            }
            return 0;
        }

        @Override // C3.L
        public final void d() {
            b bVar;
            synchronized (this) {
                try {
                    Object obj = this._heap;
                    C1.A a4 = Q.f443a;
                    if (obj == a4) {
                        return;
                    }
                    if (obj instanceof b) {
                        bVar = (b) obj;
                    } else {
                        bVar = null;
                    }
                    if (bVar != null) {
                        bVar.c(this);
                    }
                    this._heap = a4;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        @Override // F3.z
        public final F3.y<?> e() {
            Object obj = this._heap;
            if (obj instanceof F3.y) {
                return (F3.y) obj;
            }
            return null;
        }

        @Override // F3.z
        public final void f(b bVar) {
            if (this._heap != Q.f443a) {
                this._heap = bVar;
                return;
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v1, types: [T extends F3.z & java.lang.Comparable<? super T>[]] */
        /* JADX WARN: Type inference failed for: r0v5 */
        public final int g(long j4, b bVar, O o4) {
            a aVar;
            synchronized (this) {
                if (this._heap == Q.f443a) {
                    return 2;
                }
                synchronized (bVar) {
                    ?? r02 = bVar.f952a;
                    if (r02 != 0) {
                        aVar = r02[0];
                    } else {
                        aVar = null;
                    }
                    a aVar2 = aVar;
                    AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = O.f437o;
                    o4.getClass();
                    if (O.f439q.get(o4) != 0) {
                        return 1;
                    }
                    if (aVar2 == null) {
                        bVar.f442c = j4;
                    } else {
                        long j5 = aVar2.f440j;
                        if (j5 - j4 < 0) {
                            j4 = j5;
                        }
                        if (j4 - bVar.f442c > 0) {
                            bVar.f442c = j4;
                        }
                    }
                    long j6 = this.f440j;
                    long j7 = bVar.f442c;
                    if (j6 - j7 < 0) {
                        this.f440j = j7;
                    }
                    bVar.a(this);
                    return 0;
                }
            }
        }

        public final String toString() {
            return "Delayed[nanos=" + this.f440j + ']';
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class b extends F3.y<a> {

        /* renamed from: c  reason: collision with root package name */
        public long f442c;
    }

    @Override // C3.AbstractC0171v
    public final void F(n3.f fVar, Runnable runnable) {
        M(runnable);
    }

    public void M(Runnable runnable) {
        if (N(runnable)) {
            Thread K3 = K();
            if (Thread.currentThread() != K3) {
                LockSupport.unpark(K3);
                return;
            }
            return;
        }
        D.f426r.M(runnable);
    }

    public final boolean N(Runnable runnable) {
        boolean z4;
        while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f437o;
            Object obj = atomicReferenceFieldUpdater.get(this);
            if (f439q.get(this) != 0) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (z4) {
                return false;
            }
            if (obj == null) {
                while (!atomicReferenceFieldUpdater.compareAndSet(this, null, runnable)) {
                    if (atomicReferenceFieldUpdater.get(this) != null) {
                        break;
                    }
                }
                return true;
            } else if (obj instanceof F3.o) {
                F3.o oVar = (F3.o) obj;
                int a4 = oVar.a(runnable);
                if (a4 == 0) {
                    return true;
                }
                if (a4 != 1) {
                    if (a4 == 2) {
                        return false;
                    }
                } else {
                    F3.o c4 = oVar.c();
                    while (!atomicReferenceFieldUpdater.compareAndSet(this, obj, c4) && atomicReferenceFieldUpdater.get(this) == obj) {
                    }
                }
            } else if (obj == Q.f444b) {
                return false;
            } else {
                F3.o oVar2 = new F3.o(8, true);
                oVar2.a((Runnable) obj);
                oVar2.a(runnable);
                while (!atomicReferenceFieldUpdater.compareAndSet(this, obj, oVar2)) {
                    if (atomicReferenceFieldUpdater.get(this) != obj) {
                        break;
                    }
                }
                return true;
            }
        }
    }

    public final boolean O() {
        boolean z4;
        m3.a<J<?>> aVar = this.f436n;
        if (aVar != null) {
            z4 = aVar.isEmpty();
        } else {
            z4 = true;
        }
        if (!z4) {
            return false;
        }
        b bVar = (b) f438p.get(this);
        if (bVar != null && F3.y.f951b.get(bVar) != 0) {
            return false;
        }
        Object obj = f437o.get(this);
        if (obj == null) {
            return true;
        }
        if (obj instanceof F3.o) {
            long j4 = F3.o.f.get((F3.o) obj);
            if (((int) (1073741823 & j4)) == ((int) ((j4 & 1152921503533105152L) >> 30))) {
                return true;
            }
        } else if (obj == Q.f444b) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:39:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x008e A[EDGE_INSN: B:99:0x008e->B:57:0x008e ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r11v0, types: [java.lang.Object, C3.O, C3.N] */
    /* JADX WARN: Type inference failed for: r7v22 */
    /* JADX WARN: Type inference failed for: r7v9, types: [T extends F3.z & java.lang.Comparable<? super T>[]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final long P() {
        /*
            Method dump skipped, instructions count: 260
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: C3.O.P():long");
    }

    @Override // C3.N
    public void shutdown() {
        a aVar;
        ThreadLocal<N> threadLocal = m0.f493a;
        m0.f493a.set(null);
        f439q.set(this, 1);
        loop0: while (true) {
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f437o;
            Object obj = atomicReferenceFieldUpdater.get(this);
            C1.A a4 = Q.f444b;
            if (obj == null) {
                while (!atomicReferenceFieldUpdater.compareAndSet(this, null, a4)) {
                    if (atomicReferenceFieldUpdater.get(this) != null) {
                        break;
                    }
                }
                break loop0;
            } else if (obj instanceof F3.o) {
                ((F3.o) obj).b();
                break;
            } else if (obj != a4) {
                F3.o oVar = new F3.o(8, true);
                oVar.a((Runnable) obj);
                while (!atomicReferenceFieldUpdater.compareAndSet(this, obj, oVar)) {
                    if (atomicReferenceFieldUpdater.get(this) != obj) {
                        break;
                    }
                }
                break loop0;
            } else {
                break;
            }
        }
        do {
        } while (P() <= 0);
        long nanoTime = System.nanoTime();
        while (true) {
            b bVar = (b) f438p.get(this);
            if (bVar != null) {
                synchronized (bVar) {
                    if (F3.y.f951b.get(bVar) > 0) {
                        aVar = bVar.d(0);
                    } else {
                        aVar = null;
                    }
                }
                a aVar2 = aVar;
                if (aVar2 != null) {
                    L(nanoTime, aVar2);
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }
}
