package M0;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class j implements Executor {

    /* renamed from: k  reason: collision with root package name */
    public final Executor f1675k;

    /* renamed from: m  reason: collision with root package name */
    public volatile Runnable f1677m;

    /* renamed from: j  reason: collision with root package name */
    public final ArrayDeque<a> f1674j = new ArrayDeque<>();

    /* renamed from: l  reason: collision with root package name */
    public final Object f1676l = new Object();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final j f1678j;

        /* renamed from: k  reason: collision with root package name */
        public final Runnable f1679k;

        public a(j jVar, Runnable runnable) {
            this.f1678j = jVar;
            this.f1679k = runnable;
        }

        @Override // java.lang.Runnable
        public final void run() {
            j jVar = this.f1678j;
            try {
                this.f1679k.run();
            } finally {
                jVar.b();
            }
        }
    }

    public j(Executor executor) {
        this.f1675k = executor;
    }

    public final boolean a() {
        boolean z4;
        synchronized (this.f1676l) {
            z4 = !this.f1674j.isEmpty();
        }
        return z4;
    }

    public final void b() {
        synchronized (this.f1676l) {
            try {
                a poll = this.f1674j.poll();
                this.f1677m = poll;
                if (poll != null) {
                    this.f1675k.execute(this.f1677m);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        synchronized (this.f1676l) {
            try {
                this.f1674j.add(new a(this, runnable));
                if (this.f1677m == null) {
                    b();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
