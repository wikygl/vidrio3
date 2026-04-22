package j2;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class y implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    public final ThreadFactory f4854a = Executors.defaultThreadFactory();

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        Thread newThread = this.f4854a.newThread(runnable);
        newThread.setName("ScionFrontendApi");
        return newThread;
    }
}
