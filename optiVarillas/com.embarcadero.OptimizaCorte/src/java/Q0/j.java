package Q0;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class j implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    public final ThreadFactory f1980a = Executors.defaultThreadFactory();

    /* renamed from: b  reason: collision with root package name */
    public final AtomicInteger f1981b = new AtomicInteger(1);

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        AtomicInteger atomicInteger = this.f1981b;
        Thread newThread = this.f1980a.newThread(runnable);
        int andIncrement = atomicInteger.getAndIncrement();
        newThread.setName("PlayBillingLibrary-" + andIncrement);
        return newThread;
    }
}
