package A1;

import android.content.Context;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class R0 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ T0 f85j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Context f86k;

    public /* synthetic */ R0(T0 t02, Context context) {
        this.f85j = t02;
        this.f86k = context;
    }

    @Override // java.lang.Runnable
    public final void run() {
        T0 t02 = this.f85j;
        Context context = this.f86k;
        synchronized (t02.f93e) {
            t02.e(context);
        }
    }
}
