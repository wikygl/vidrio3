package i1;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import m1.C0736a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class q implements Executor {

    /* renamed from: j  reason: collision with root package name */
    public final Executor f3654j;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final Runnable f3655j;

        public a(Runnable runnable) {
            this.f3655j = runnable;
        }

        @Override // java.lang.Runnable
        public final void run() {
            try {
                this.f3655j.run();
            } catch (Exception e4) {
                C0736a.b("Executor", "Background execution failure.", e4);
            }
        }
    }

    public q(ExecutorService executorService) {
        this.f3654j = executorService;
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f3654j.execute(new a(runnable));
    }
}
