package i2;

import e0.C0405a;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class F implements Executor {

    /* renamed from: k  reason: collision with root package name */
    public final ThreadPoolExecutor f3677k;

    /* renamed from: j  reason: collision with root package name */
    public final AtomicInteger f3676j = new AtomicInteger(1);

    /* renamed from: l  reason: collision with root package name */
    public WeakReference f3678l = new WeakReference(null);

    public F() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new ThreadFactory() { // from class: i2.E
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                F f = F.this;
                Thread thread = new Thread(runnable, C0405a.c("Google consent worker #", f.f3676j.getAndIncrement()));
                f.f3678l = new WeakReference(thread);
                return thread;
            }
        });
        this.f3677k = threadPoolExecutor;
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        if (Thread.currentThread() == this.f3678l.get()) {
            runnable.run();
        } else {
            this.f3677k.execute(runnable);
        }
    }
}
