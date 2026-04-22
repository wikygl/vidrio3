package C3;

import C3.O;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class D extends O implements Runnable {
    private static volatile Thread _thread;
    private static volatile int debugStatus;

    /* renamed from: r  reason: collision with root package name */
    public static final D f426r;

    /* renamed from: s  reason: collision with root package name */
    public static final long f427s;

    /* JADX WARN: Type inference failed for: r0v0, types: [C3.D, C3.O, C3.N] */
    static {
        Long l2;
        ?? o4 = new O();
        f426r = o4;
        o4.I(false);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        try {
            l2 = Long.getLong("kotlinx.coroutines.DefaultExecutor.keepAlive", 1000L);
        } catch (SecurityException unused) {
            l2 = 1000L;
        }
        f427s = timeUnit.toNanos(l2.longValue());
    }

    @Override // C3.P
    public final Thread K() {
        Thread thread = _thread;
        if (thread == null) {
            synchronized (this) {
                thread = _thread;
                if (thread == null) {
                    thread = new Thread(this, "kotlinx.coroutines.DefaultExecutor");
                    _thread = thread;
                    thread.setDaemon(true);
                    thread.start();
                }
            }
        }
        return thread;
    }

    @Override // C3.P
    public final void L(long j4, O.a aVar) {
        throw new RejectedExecutionException("DefaultExecutor was shut down. This error indicates that Dispatchers.shutdown() was invoked prior to completion of exiting coroutines, leaving coroutines in incomplete state. Please refer to Dispatchers.shutdown documentation for more details");
    }

    @Override // C3.O
    public final void M(Runnable runnable) {
        if (debugStatus != 4) {
            super.M(runnable);
            return;
        }
        throw new RejectedExecutionException("DefaultExecutor was shut down. This error indicates that Dispatchers.shutdown() was invoked prior to completion of exiting coroutines, leaving coroutines in incomplete state. Please refer to Dispatchers.shutdown documentation for more details");
    }

    public final synchronized void Q() {
        int i4 = debugStatus;
        if (i4 != 2 && i4 != 3) {
            return;
        }
        debugStatus = 3;
        O.f437o.set(this, null);
        O.f438p.set(this, null);
        notifyAll();
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean O3;
        m0.f493a.set(this);
        try {
            synchronized (this) {
                int i4 = debugStatus;
                if (i4 != 2 && i4 != 3) {
                    debugStatus = 1;
                    notifyAll();
                    long j4 = Long.MAX_VALUE;
                    while (true) {
                        Thread.interrupted();
                        long P3 = P();
                        if (P3 == Long.MAX_VALUE) {
                            long nanoTime = System.nanoTime();
                            if (j4 == Long.MAX_VALUE) {
                                j4 = f427s + nanoTime;
                            }
                            long j5 = j4 - nanoTime;
                            if (j5 <= 0) {
                                _thread = null;
                                Q();
                                if (!O()) {
                                    K();
                                    return;
                                }
                                return;
                            } else if (P3 > j5) {
                                P3 = j5;
                            }
                        } else {
                            j4 = Long.MAX_VALUE;
                        }
                        if (P3 > 0) {
                            int i5 = debugStatus;
                            if (i5 == 2 || i5 == 3) {
                                break;
                            }
                            LockSupport.parkNanos(this, P3);
                        }
                    }
                    if (!O3) {
                        return;
                    }
                    return;
                }
                _thread = null;
                Q();
                if (!O()) {
                    K();
                }
            }
        } finally {
            _thread = null;
            Q();
            if (!O()) {
                K();
            }
        }
    }

    @Override // C3.O, C3.N
    public final void shutdown() {
        debugStatus = 4;
        super.shutdown();
    }
}
