package O0;

import M0.j;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b implements O0.a {

    /* renamed from: a  reason: collision with root package name */
    public final j f1794a;

    /* renamed from: b  reason: collision with root package name */
    public final Handler f1795b = new Handler(Looper.getMainLooper());

    /* renamed from: c  reason: collision with root package name */
    public final a f1796c = new a();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a implements Executor {
        public a() {
        }

        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            b.this.f1795b.post(runnable);
        }
    }

    public b(Executor executor) {
        this.f1794a = new j(executor);
    }

    public final void a(Runnable runnable) {
        this.f1794a.execute(runnable);
    }
}
