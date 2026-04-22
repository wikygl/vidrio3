package B;

import B.i;
import android.app.Application;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class g implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Application f221j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ i.a f222k;

    public g(Application application, i.a aVar) {
        this.f221j = application;
        this.f222k = aVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f221j.unregisterActivityLifecycleCallbacks(this.f222k);
    }
}
