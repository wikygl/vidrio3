package G3;

import C1.A;
import C3.C;
import F3.n;
import F3.t;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a implements Executor, Closeable {

    /* renamed from: q  reason: collision with root package name */
    public static final AtomicLongFieldUpdater f963q = AtomicLongFieldUpdater.newUpdater(a.class, "parkedWorkersStack");

    /* renamed from: r  reason: collision with root package name */
    public static final AtomicLongFieldUpdater f964r = AtomicLongFieldUpdater.newUpdater(a.class, "controlState");

    /* renamed from: s  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f965s = AtomicIntegerFieldUpdater.newUpdater(a.class, "_isTerminated");

    /* renamed from: t  reason: collision with root package name */
    public static final A f966t = new A(1, "NOT_IN_STACK");
    private volatile int _isTerminated;
    private volatile long controlState;

    /* renamed from: j  reason: collision with root package name */
    public final int f967j;

    /* renamed from: k  reason: collision with root package name */
    public final int f968k;

    /* renamed from: l  reason: collision with root package name */
    public final long f969l;

    /* renamed from: m  reason: collision with root package name */
    public final String f970m;

    /* renamed from: n  reason: collision with root package name */
    public final d f971n;

    /* renamed from: o  reason: collision with root package name */
    public final d f972o;

    /* renamed from: p  reason: collision with root package name */
    public final t<C0007a> f973p;
    private volatile long parkedWorkersStack;

    /* renamed from: G3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public final class C0007a extends Thread {

        /* renamed from: r  reason: collision with root package name */
        public static final AtomicIntegerFieldUpdater f974r = AtomicIntegerFieldUpdater.newUpdater(C0007a.class, "workerCtl");
        private volatile int indexInArray;

        /* renamed from: j  reason: collision with root package name */
        public final m f975j;

        /* renamed from: k  reason: collision with root package name */
        public final v3.m<h> f976k;

        /* renamed from: l  reason: collision with root package name */
        public b f977l;

        /* renamed from: m  reason: collision with root package name */
        public long f978m;

        /* renamed from: n  reason: collision with root package name */
        public long f979n;
        private volatile Object nextParkedWorker;

        /* renamed from: o  reason: collision with root package name */
        public int f980o;

        /* renamed from: p  reason: collision with root package name */
        public boolean f981p;
        private volatile int workerCtl;

        public C0007a() {
            throw null;
        }

        /* JADX WARN: Type inference failed for: r1v3, types: [v3.m<G3.h>, java.lang.Object] */
        public C0007a(int i4) {
            setDaemon(true);
            this.f975j = new m();
            this.f976k = new Object();
            this.f977l = b.f986m;
            this.nextParkedWorker = a.f966t;
            w3.c.f6413j.getClass();
            this.f980o = w3.c.f6414k.a();
            f(i4);
        }

        public final h a(boolean z4) {
            h e4;
            h e5;
            a aVar;
            long j4;
            b bVar = this.f977l;
            b bVar2 = b.f983j;
            h hVar = null;
            m mVar = this.f975j;
            boolean z5 = true;
            a aVar2 = a.this;
            if (bVar != bVar2) {
                AtomicLongFieldUpdater atomicLongFieldUpdater = a.f964r;
                do {
                    aVar = a.this;
                    j4 = atomicLongFieldUpdater.get(aVar);
                    if (((int) ((9223367638808264704L & j4) >> 42)) == 0) {
                        mVar.getClass();
                        loop1: while (true) {
                            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = m.f1007b;
                            h hVar2 = (h) atomicReferenceFieldUpdater.get(mVar);
                            if (hVar2 != null && hVar2.f996k.a() == 1) {
                                while (!atomicReferenceFieldUpdater.compareAndSet(mVar, hVar2, null)) {
                                    if (atomicReferenceFieldUpdater.get(mVar) != hVar2) {
                                        break;
                                    }
                                }
                                hVar = hVar2;
                                break loop1;
                            }
                        }
                        int i4 = m.f1009d.get(mVar);
                        int i5 = m.f1008c.get(mVar);
                        while (true) {
                            if (i4 != i5 && m.f1010e.get(mVar) != 0) {
                                i5--;
                                h c4 = mVar.c(i5, true);
                                if (c4 != null) {
                                    hVar = c4;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (hVar == null) {
                            h d4 = aVar2.f972o.d();
                            if (d4 == null) {
                                return i(1);
                            }
                            return d4;
                        }
                        return hVar;
                    }
                } while (!a.f964r.compareAndSet(aVar, j4, j4 - 4398046511104L));
                this.f977l = bVar2;
            }
            if (z4) {
                if (d(aVar2.f967j * 2) != 0) {
                    z5 = false;
                }
                if (!z5 || (e5 = e()) == null) {
                    mVar.getClass();
                    h hVar3 = (h) m.f1007b.getAndSet(mVar, null);
                    if (hVar3 == null) {
                        hVar3 = mVar.b();
                    }
                    if (hVar3 == null) {
                        if (!z5 && (e4 = e()) != null) {
                            return e4;
                        }
                    } else {
                        return hVar3;
                    }
                } else {
                    return e5;
                }
            } else {
                h e6 = e();
                if (e6 != null) {
                    return e6;
                }
            }
            return i(3);
        }

        public final int b() {
            return this.indexInArray;
        }

        public final Object c() {
            return this.nextParkedWorker;
        }

        public final int d(int i4) {
            int i5 = this.f980o;
            int i6 = i5 ^ (i5 << 13);
            int i7 = i6 ^ (i6 >> 17);
            int i8 = i7 ^ (i7 << 5);
            this.f980o = i8;
            int i9 = i4 - 1;
            if ((i9 & i4) == 0) {
                return i8 & i9;
            }
            return (i8 & Integer.MAX_VALUE) % i4;
        }

        public final h e() {
            int d4 = d(2);
            a aVar = a.this;
            if (d4 == 0) {
                h d5 = aVar.f971n.d();
                if (d5 != null) {
                    return d5;
                }
                return aVar.f972o.d();
            }
            h d6 = aVar.f972o.d();
            if (d6 != null) {
                return d6;
            }
            return aVar.f971n.d();
        }

        public final void f(int i4) {
            String valueOf;
            StringBuilder sb = new StringBuilder();
            sb.append(a.this.f970m);
            sb.append("-worker-");
            if (i4 == 0) {
                valueOf = "TERMINATED";
            } else {
                valueOf = String.valueOf(i4);
            }
            sb.append(valueOf);
            setName(sb.toString());
            this.indexInArray = i4;
        }

        public final void g(Object obj) {
            this.nextParkedWorker = obj;
        }

        public final boolean h(b bVar) {
            boolean z4;
            b bVar2 = this.f977l;
            if (bVar2 == b.f983j) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (z4) {
                a.f964r.addAndGet(a.this, 4398046511104L);
            }
            if (bVar2 != bVar) {
                this.f977l = bVar;
            }
            return z4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0082, code lost:
            r19 = r6;
            r6 = -2;
            r5 = r5;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r14v1, types: [T, G3.h, java.lang.Object] */
        /* JADX WARN: Type inference failed for: r7v4 */
        /* JADX WARN: Type inference failed for: r7v5, types: [G3.h] */
        /* JADX WARN: Type inference failed for: r7v9, types: [G3.h] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final G3.h i(int r24) {
            /*
                Method dump skipped, instructions count: 251
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: G3.a.C0007a.i(int):G3.h");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            boolean z4;
            AtomicIntegerFieldUpdater atomicIntegerFieldUpdater;
            boolean z5;
            boolean z6 = false;
            loop0: while (true) {
                boolean z7 = false;
                while (true) {
                    a aVar = a.this;
                    aVar.getClass();
                    if (a.f965s.get(aVar) == 0) {
                        b bVar = this.f977l;
                        b bVar2 = b.f987n;
                        if (bVar == bVar2) {
                            break loop0;
                        }
                        h a4 = a(this.f981p);
                        long j4 = -2097152;
                        if (a4 != null) {
                            this.f979n = 0L;
                            int a5 = a4.f996k.a();
                            this.f978m = 0L;
                            b bVar3 = this.f977l;
                            b bVar4 = b.f985l;
                            b bVar5 = b.f984k;
                            if (bVar3 == bVar4) {
                                this.f977l = bVar5;
                            }
                            a aVar2 = a.this;
                            if (a5 != 0 && h(bVar5) && !aVar2.g() && !aVar2.f(a.f964r.get(aVar2))) {
                                aVar2.g();
                            }
                            aVar2.getClass();
                            try {
                                a4.run();
                            } catch (Throwable th) {
                                Thread currentThread = Thread.currentThread();
                                currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, th);
                            }
                            if (a5 != 0) {
                                a.f964r.addAndGet(aVar2, -2097152L);
                                if (this.f977l != bVar2) {
                                    this.f977l = b.f986m;
                                }
                            }
                        } else {
                            this.f981p = z6;
                            if (this.f979n != 0) {
                                if (!z7) {
                                    z7 = true;
                                } else {
                                    h(b.f985l);
                                    Thread.interrupted();
                                    LockSupport.parkNanos(this.f979n);
                                    this.f979n = 0L;
                                    break;
                                }
                            } else {
                                Object obj = this.nextParkedWorker;
                                A a6 = a.f966t;
                                if (obj != a6) {
                                    z4 = true;
                                } else {
                                    z4 = false;
                                }
                                if (!z4) {
                                    a aVar3 = a.this;
                                    aVar3.getClass();
                                    if (this.nextParkedWorker == a6) {
                                        while (true) {
                                            AtomicLongFieldUpdater atomicLongFieldUpdater = a.f963q;
                                            long j5 = atomicLongFieldUpdater.get(aVar3);
                                            int i4 = this.indexInArray;
                                            this.nextParkedWorker = aVar3.f973p.b((int) (j5 & 2097151));
                                            if (atomicLongFieldUpdater.compareAndSet(aVar3, j5, ((2097152 + j5) & j4) | i4)) {
                                                break;
                                            }
                                            j4 = -2097152;
                                        }
                                    }
                                } else {
                                    f974r.set(this, -1);
                                    while (this.nextParkedWorker != a.f966t) {
                                        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater2 = f974r;
                                        if (atomicIntegerFieldUpdater2.get(this) != -1) {
                                            break;
                                        }
                                        a aVar4 = a.this;
                                        aVar4.getClass();
                                        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater3 = a.f965s;
                                        if (atomicIntegerFieldUpdater3.get(aVar4) != 0) {
                                            break;
                                        }
                                        b bVar6 = this.f977l;
                                        b bVar7 = b.f987n;
                                        if (bVar6 == bVar7) {
                                            break;
                                        }
                                        h(b.f985l);
                                        Thread.interrupted();
                                        if (this.f978m == 0) {
                                            atomicIntegerFieldUpdater = atomicIntegerFieldUpdater2;
                                            this.f978m = System.nanoTime() + a.this.f969l;
                                        } else {
                                            atomicIntegerFieldUpdater = atomicIntegerFieldUpdater2;
                                        }
                                        LockSupport.parkNanos(a.this.f969l);
                                        if (System.nanoTime() - this.f978m >= 0) {
                                            this.f978m = 0L;
                                            a aVar5 = a.this;
                                            synchronized (aVar5.f973p) {
                                                try {
                                                    if (atomicIntegerFieldUpdater3.get(aVar5) != 0) {
                                                        z5 = true;
                                                    } else {
                                                        z5 = false;
                                                    }
                                                    if (!z5) {
                                                        AtomicLongFieldUpdater atomicLongFieldUpdater2 = a.f964r;
                                                        if (((int) (atomicLongFieldUpdater2.get(aVar5) & 2097151)) > aVar5.f967j) {
                                                            if (atomicIntegerFieldUpdater.compareAndSet(this, -1, 1)) {
                                                                int i5 = this.indexInArray;
                                                                f(0);
                                                                aVar5.d(this, i5, 0);
                                                                int andDecrement = (int) (atomicLongFieldUpdater2.getAndDecrement(aVar5) & 2097151);
                                                                if (andDecrement != i5) {
                                                                    C0007a b4 = aVar5.f973p.b(andDecrement);
                                                                    v3.h.b(b4);
                                                                    C0007a c0007a = b4;
                                                                    aVar5.f973p.c(i5, c0007a);
                                                                    c0007a.f(i5);
                                                                    aVar5.d(c0007a, andDecrement, i5);
                                                                }
                                                                aVar5.f973p.c(andDecrement, null);
                                                                this.f977l = bVar7;
                                                            }
                                                        }
                                                    }
                                                } catch (Throwable th2) {
                                                    throw th2;
                                                }
                                            }
                                        }
                                    }
                                }
                                z6 = false;
                            }
                        }
                    } else {
                        break loop0;
                    }
                }
            }
            h(b.f987n);
        }
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class b {

        /* renamed from: j  reason: collision with root package name */
        public static final b f983j;

        /* renamed from: k  reason: collision with root package name */
        public static final b f984k;

        /* renamed from: l  reason: collision with root package name */
        public static final b f985l;

        /* renamed from: m  reason: collision with root package name */
        public static final b f986m;

        /* renamed from: n  reason: collision with root package name */
        public static final b f987n;

        /* renamed from: o  reason: collision with root package name */
        public static final /* synthetic */ b[] f988o;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r5v0, types: [G3.a$b, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r6v1, types: [G3.a$b, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r7v1, types: [G3.a$b, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r8v1, types: [G3.a$b, java.lang.Enum] */
        /* JADX WARN: Type inference failed for: r9v1, types: [G3.a$b, java.lang.Enum] */
        static {
            ?? r5 = new Enum("CPU_ACQUIRED", 0);
            f983j = r5;
            ?? r6 = new Enum("BLOCKING", 1);
            f984k = r6;
            ?? r7 = new Enum("PARKING", 2);
            f985l = r7;
            ?? r8 = new Enum("DORMANT", 3);
            f986m = r8;
            ?? r9 = new Enum("TERMINATED", 4);
            f987n = r9;
            f988o = new b[]{r5, r6, r7, r8, r9};
        }

        public b() {
            throw null;
        }

        public static b valueOf(String str) {
            return (b) Enum.valueOf(b.class, str);
        }

        public static b[] values() {
            return (b[]) f988o.clone();
        }
    }

    /* JADX WARN: Type inference failed for: r4v10, types: [G3.d, F3.n] */
    /* JADX WARN: Type inference failed for: r4v9, types: [G3.d, F3.n] */
    public a(int i4, int i5, long j4, String str) {
        this.f967j = i4;
        this.f968k = i5;
        this.f969l = j4;
        this.f970m = str;
        if (i4 >= 1) {
            if (i5 >= i4) {
                if (i5 <= 2097150) {
                    if (j4 > 0) {
                        this.f971n = new n();
                        this.f972o = new n();
                        this.f973p = new t<>((i4 + 1) * 2);
                        this.controlState = i4 << 42;
                        this._isTerminated = 0;
                        return;
                    }
                    throw new IllegalArgumentException(("Idle worker keep alive time " + j4 + " must be positive").toString());
                }
                throw new IllegalArgumentException(I.h.b(i5, "Max pool size ", " should not exceed maximal supported number of threads 2097150").toString());
            }
            throw new IllegalArgumentException(X1.b.d(i5, i4, "Max pool size ", " should be greater than or equals to core pool size ").toString());
        }
        throw new IllegalArgumentException(I.h.b(i4, "Core pool size ", " should be at least 1").toString());
    }

    public final int a() {
        synchronized (this.f973p) {
            try {
                if (f965s.get(this) != 0) {
                    return -1;
                }
                AtomicLongFieldUpdater atomicLongFieldUpdater = f964r;
                long j4 = atomicLongFieldUpdater.get(this);
                int i4 = (int) (j4 & 2097151);
                int i5 = i4 - ((int) ((j4 & 4398044413952L) >> 21));
                if (i5 < 0) {
                    i5 = 0;
                }
                if (i5 >= this.f967j) {
                    return 0;
                }
                if (i4 >= this.f968k) {
                    return 0;
                }
                int i6 = ((int) (atomicLongFieldUpdater.get(this) & 2097151)) + 1;
                if (i6 > 0 && this.f973p.b(i6) == null) {
                    C0007a c0007a = new C0007a(i6);
                    this.f973p.c(i6, c0007a);
                    if (i6 == ((int) (2097151 & atomicLongFieldUpdater.incrementAndGet(this)))) {
                        int i7 = i5 + 1;
                        c0007a.start();
                        return i7;
                    }
                    throw new IllegalArgumentException("Failed requirement.".toString());
                }
                throw new IllegalArgumentException("Failed requirement.".toString());
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void b(Runnable runnable, i iVar, boolean z4) {
        h jVar;
        boolean z5;
        long j4;
        C0007a c0007a;
        boolean a4;
        k.f.getClass();
        long nanoTime = System.nanoTime();
        if (runnable instanceof h) {
            jVar = (h) runnable;
            jVar.f995j = nanoTime;
            jVar.f996k = iVar;
        } else {
            jVar = new j(runnable, nanoTime, iVar);
        }
        boolean z6 = false;
        if (jVar.f996k.a() == 1) {
            z5 = true;
        } else {
            z5 = false;
        }
        AtomicLongFieldUpdater atomicLongFieldUpdater = f964r;
        if (z5) {
            j4 = atomicLongFieldUpdater.addAndGet(this, 2097152L);
        } else {
            j4 = 0;
        }
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof C0007a) {
            c0007a = (C0007a) currentThread;
        } else {
            c0007a = null;
        }
        if (c0007a == null || !v3.h.a(a.this, this)) {
            c0007a = null;
        }
        if (c0007a != null && c0007a.f977l != b.f987n && (jVar.f996k.a() != 0 || c0007a.f977l != b.f984k)) {
            c0007a.f981p = true;
            m mVar = c0007a.f975j;
            if (z4) {
                jVar = mVar.a(jVar);
            } else {
                mVar.getClass();
                h hVar = (h) m.f1007b.getAndSet(mVar, jVar);
                if (hVar == null) {
                    jVar = null;
                } else {
                    jVar = mVar.a(hVar);
                }
            }
        }
        if (jVar != null) {
            if (jVar.f996k.a() == 1) {
                a4 = this.f972o.a(jVar);
            } else {
                a4 = this.f971n.a(jVar);
            }
            if (!a4) {
                throw new RejectedExecutionException(C.b.c(new StringBuilder(), this.f970m, " was terminated"));
            }
        }
        if (z4 && c0007a != null) {
            z6 = true;
        }
        if (z5) {
            if (!z6 && !g() && !f(j4)) {
                g();
            }
        } else if (!z6 && !g() && !f(atomicLongFieldUpdater.get(this))) {
            g();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:38:0x0087, code lost:
        if (r1 == null) goto L46;
     */
    @Override // java.io.Closeable, java.lang.AutoCloseable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void close() {
        /*
            r8 = this;
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r0 = G3.a.f965s
            r1 = 0
            r2 = 1
            boolean r0 = r0.compareAndSet(r8, r1, r2)
            if (r0 != 0) goto Lc
            goto Lb0
        Lc:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            boolean r1 = r0 instanceof G3.a.C0007a
            r3 = 0
            if (r1 == 0) goto L18
            G3.a$a r0 = (G3.a.C0007a) r0
            goto L19
        L18:
            r0 = r3
        L19:
            if (r0 == 0) goto L24
            G3.a r1 = G3.a.this
            boolean r1 = v3.h.a(r1, r8)
            if (r1 == 0) goto L24
            goto L25
        L24:
            r0 = r3
        L25:
            F3.t<G3.a$a> r1 = r8.f973p
            monitor-enter(r1)
            java.util.concurrent.atomic.AtomicLongFieldUpdater r4 = G3.a.f964r     // Catch: java.lang.Throwable -> Lc2
            long r4 = r4.get(r8)     // Catch: java.lang.Throwable -> Lc2
            r6 = 2097151(0x1fffff, double:1.0361303E-317)
            long r4 = r4 & r6
            int r5 = (int) r4
            monitor-exit(r1)
            if (r2 > r5) goto L77
            r1 = 1
        L37:
            F3.t<G3.a$a> r4 = r8.f973p
            java.lang.Object r4 = r4.b(r1)
            v3.h.b(r4)
            G3.a$a r4 = (G3.a.C0007a) r4
            if (r4 == r0) goto L72
        L44:
            boolean r6 = r4.isAlive()
            if (r6 == 0) goto L53
            java.util.concurrent.locks.LockSupport.unpark(r4)
            r6 = 10000(0x2710, double:4.9407E-320)
            r4.join(r6)
            goto L44
        L53:
            G3.m r4 = r4.f975j
            G3.d r6 = r8.f972o
            r4.getClass()
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r7 = G3.m.f1007b
            java.lang.Object r7 = r7.getAndSet(r4, r3)
            G3.h r7 = (G3.h) r7
            if (r7 == 0) goto L67
            r6.a(r7)
        L67:
            G3.h r7 = r4.b()
            if (r7 != 0) goto L6e
            goto L72
        L6e:
            r6.a(r7)
            goto L67
        L72:
            if (r1 == r5) goto L77
            int r1 = r1 + 1
            goto L37
        L77:
            G3.d r1 = r8.f972o
            r1.b()
            G3.d r1 = r8.f971n
            r1.b()
        L81:
            if (r0 == 0) goto L89
            G3.h r1 = r0.a(r2)
            if (r1 != 0) goto Lb1
        L89:
            G3.d r1 = r8.f971n
            java.lang.Object r1 = r1.d()
            G3.h r1 = (G3.h) r1
            if (r1 != 0) goto Lb1
            G3.d r1 = r8.f972o
            java.lang.Object r1 = r1.d()
            G3.h r1 = (G3.h) r1
            if (r1 != 0) goto Lb1
            if (r0 == 0) goto La4
            G3.a$b r1 = G3.a.b.f987n
            r0.h(r1)
        La4:
            java.util.concurrent.atomic.AtomicLongFieldUpdater r0 = G3.a.f963q
            r1 = 0
            r0.set(r8, r1)
            java.util.concurrent.atomic.AtomicLongFieldUpdater r0 = G3.a.f964r
            r0.set(r8, r1)
        Lb0:
            return
        Lb1:
            r1.run()     // Catch: java.lang.Throwable -> Lb5
            goto L81
        Lb5:
            r1 = move-exception
            java.lang.Thread r3 = java.lang.Thread.currentThread()
            java.lang.Thread$UncaughtExceptionHandler r4 = r3.getUncaughtExceptionHandler()
            r4.uncaughtException(r3, r1)
            goto L81
        Lc2:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: G3.a.close():void");
    }

    public final void d(C0007a c0007a, int i4, int i5) {
        while (true) {
            long j4 = f963q.get(this);
            int i6 = (int) (2097151 & j4);
            long j5 = (2097152 + j4) & (-2097152);
            if (i6 == i4) {
                if (i5 == 0) {
                    Object c4 = c0007a.c();
                    while (true) {
                        if (c4 == f966t) {
                            i6 = -1;
                            break;
                        } else if (c4 == null) {
                            i6 = 0;
                            break;
                        } else {
                            C0007a c0007a2 = (C0007a) c4;
                            i6 = c0007a2.b();
                            if (i6 != 0) {
                                break;
                            }
                            c4 = c0007a2.c();
                        }
                    }
                } else {
                    i6 = i5;
                }
            }
            if (i6 >= 0) {
                if (f963q.compareAndSet(this, j4, j5 | i6)) {
                    return;
                }
            }
        }
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        b(runnable, k.f1004g, false);
    }

    public final boolean f(long j4) {
        int i4 = ((int) (2097151 & j4)) - ((int) ((j4 & 4398044413952L) >> 21));
        if (i4 < 0) {
            i4 = 0;
        }
        int i5 = this.f967j;
        if (i4 < i5) {
            int a4 = a();
            if (a4 == 1 && i5 > 1) {
                a();
            }
            if (a4 > 0) {
                return true;
            }
        }
        return false;
    }

    public final boolean g() {
        A a4;
        int i4;
        while (true) {
            AtomicLongFieldUpdater atomicLongFieldUpdater = f963q;
            long j4 = atomicLongFieldUpdater.get(this);
            C0007a b4 = this.f973p.b((int) (2097151 & j4));
            if (b4 == null) {
                b4 = null;
            } else {
                long j5 = (2097152 + j4) & (-2097152);
                Object c4 = b4.c();
                while (true) {
                    a4 = f966t;
                    if (c4 == a4) {
                        i4 = -1;
                        break;
                    } else if (c4 == null) {
                        i4 = 0;
                        break;
                    } else {
                        C0007a c0007a = (C0007a) c4;
                        i4 = c0007a.b();
                        if (i4 != 0) {
                            break;
                        }
                        c4 = c0007a.c();
                    }
                }
                if (i4 >= 0 && atomicLongFieldUpdater.compareAndSet(this, j4, j5 | i4)) {
                    b4.g(a4);
                }
            }
            if (b4 == null) {
                return false;
            }
            if (C0007a.f974r.compareAndSet(b4, -1, 0)) {
                LockSupport.unpark(b4);
                return true;
            }
        }
    }

    public final String toString() {
        int i4;
        ArrayList arrayList = new ArrayList();
        t<C0007a> tVar = this.f973p;
        int a4 = tVar.a();
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        for (int i10 = 1; i10 < a4; i10++) {
            C0007a b4 = tVar.b(i10);
            if (b4 != null) {
                m mVar = b4.f975j;
                mVar.getClass();
                if (m.f1007b.get(mVar) != null) {
                    i4 = (m.f1008c.get(mVar) - m.f1009d.get(mVar)) + 1;
                } else {
                    i4 = m.f1008c.get(mVar) - m.f1009d.get(mVar);
                }
                int ordinal = b4.f977l.ordinal();
                if (ordinal != 0) {
                    if (ordinal != 1) {
                        if (ordinal != 2) {
                            if (ordinal != 3) {
                                if (ordinal == 4) {
                                    i9++;
                                }
                            } else {
                                i8++;
                                if (i4 > 0) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(i4);
                                    sb.append('d');
                                    arrayList.add(sb.toString());
                                }
                            }
                        } else {
                            i7++;
                        }
                    } else {
                        i6++;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(i4);
                        sb2.append('b');
                        arrayList.add(sb2.toString());
                    }
                } else {
                    i5++;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(i4);
                    sb3.append('c');
                    arrayList.add(sb3.toString());
                }
            }
        }
        long j4 = f964r.get(this);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(this.f970m);
        sb4.append('@');
        sb4.append(C.c(this));
        sb4.append("[Pool Size {core = ");
        int i11 = this.f967j;
        sb4.append(i11);
        sb4.append(", max = ");
        sb4.append(this.f968k);
        sb4.append("}, Worker States {CPU = ");
        sb4.append(i5);
        sb4.append(", blocking = ");
        sb4.append(i6);
        sb4.append(", parked = ");
        sb4.append(i7);
        sb4.append(", dormant = ");
        sb4.append(i8);
        sb4.append(", terminated = ");
        sb4.append(i9);
        sb4.append("}, running workers queues = ");
        sb4.append(arrayList);
        sb4.append(", global CPU queue size = ");
        sb4.append(this.f971n.c());
        sb4.append(", global blocking queue size = ");
        sb4.append(this.f972o.c());
        sb4.append(", Control State {created workers= ");
        sb4.append((int) (2097151 & j4));
        sb4.append(", blocking tasks = ");
        sb4.append((int) ((4398044413952L & j4) >> 21));
        sb4.append(", CPUs acquired = ");
        sb4.append(i11 - ((int) ((j4 & 9223367638808264704L) >> 42)));
        sb4.append("}]");
        return sb4.toString();
    }
}
