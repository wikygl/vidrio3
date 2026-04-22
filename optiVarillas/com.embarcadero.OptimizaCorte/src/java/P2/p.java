package p2;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import k2.HandlerC0687a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class p implements Executor {

    /* renamed from: j  reason: collision with root package name */
    public final HandlerC0687a f5571j;

    /* JADX WARN: Type inference failed for: r0v0, types: [android.os.Handler, k2.a] */
    public p() {
        ?? handler = new Handler(Looper.getMainLooper());
        Looper.getMainLooper();
        this.f5571j = handler;
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f5571j.post(runnable);
    }
}
