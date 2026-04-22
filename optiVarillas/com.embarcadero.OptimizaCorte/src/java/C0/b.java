package C0;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    public final AtomicInteger f301a = new AtomicInteger(0);

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ boolean f302b;

    public b(boolean z4) {
        this.f302b = z4;
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        String str;
        if (this.f302b) {
            str = "WM.task-";
        } else {
            str = "androidx.work-";
        }
        return new Thread(runnable, str + this.f301a.incrementAndGet());
    }
}
