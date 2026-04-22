package E1;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    public final AtomicInteger f849a = new AtomicInteger(1);

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ String f850b;

    public b(String str) {
        this.f850b = str;
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        int andIncrement = this.f849a.getAndIncrement();
        return new Thread(runnable, "AdWorker(" + this.f850b + ") #" + andIncrement);
    }
}
