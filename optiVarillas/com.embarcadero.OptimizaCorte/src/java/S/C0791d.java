package s;

import a3.InterfaceFutureC0346a;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import s.AbstractC0788a;

/* renamed from: s.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0791d<T> implements InterfaceFutureC0346a<T> {

    /* renamed from: j  reason: collision with root package name */
    public final WeakReference<C0789b<T>> f5753j;

    /* renamed from: k  reason: collision with root package name */
    public final a f5754k = new a();

    /* renamed from: s.d$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0788a<T> {
        public a() {
        }

        @Override // s.AbstractC0788a
        public final String g() {
            C0789b<T> c0789b = C0791d.this.f5753j.get();
            if (c0789b == null) {
                return "Completer object has been garbage collected, future will fail soon";
            }
            return "tag=[" + c0789b.f5749a + "]";
        }
    }

    public C0791d(C0789b<T> c0789b) {
        this.f5753j = new WeakReference<>(c0789b);
    }

    @Override // a3.InterfaceFutureC0346a
    public final void a(Runnable runnable, Executor executor) {
        this.f5754k.a(runnable, executor);
    }

    @Override // java.util.concurrent.Future
    public final boolean cancel(boolean z4) {
        C0789b<T> c0789b = this.f5753j.get();
        boolean cancel = this.f5754k.cancel(z4);
        if (cancel && c0789b != null) {
            c0789b.f5749a = null;
            c0789b.f5750b = null;
            c0789b.f5751c.j(null);
        }
        return cancel;
    }

    @Override // java.util.concurrent.Future
    public final T get() {
        return this.f5754k.get();
    }

    @Override // java.util.concurrent.Future
    public final boolean isCancelled() {
        return this.f5754k.f5730j instanceof AbstractC0788a.b;
    }

    @Override // java.util.concurrent.Future
    public final boolean isDone() {
        return this.f5754k.isDone();
    }

    public final String toString() {
        return this.f5754k.toString();
    }

    @Override // java.util.concurrent.Future
    public final T get(long j4, TimeUnit timeUnit) {
        return this.f5754k.get(j4, timeUnit);
    }
}
