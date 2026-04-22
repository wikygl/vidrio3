package J;

import android.os.Handler;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class n<T> implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public Callable<T> f1189j;

    /* renamed from: k  reason: collision with root package name */
    public L.a<T> f1190k;

    /* renamed from: l  reason: collision with root package name */
    public Handler f1191l;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final /* synthetic */ L.a f1192j;

        /* renamed from: k  reason: collision with root package name */
        public final /* synthetic */ Object f1193k;

        public a(i iVar, Object obj) {
            this.f1192j = iVar;
            this.f1193k = obj;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.Runnable
        public final void run() {
            this.f1192j.a(this.f1193k);
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        T t3;
        try {
            t3 = this.f1189j.call();
        } catch (Exception unused) {
            t3 = null;
        }
        this.f1191l.post(new a((i) this.f1190k, t3));
    }
}
