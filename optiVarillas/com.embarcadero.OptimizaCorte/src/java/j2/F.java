package j2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class F implements Application.ActivityLifecycleCallbacks {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ G f4782j;

    public F(G g4) {
        this.f4782j = g4;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityCreated(Activity activity, Bundle bundle) {
        this.f4782j.b(new D(this, bundle, activity));
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityDestroyed(Activity activity) {
        this.f4782j.b(new p(this, activity));
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityPaused(Activity activity) {
        this.f4782j.b(new E(this, activity));
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityResumed(Activity activity) {
        this.f4782j.b(new u(this, activity));
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        BinderC0672b binderC0672b = new BinderC0672b();
        this.f4782j.b(new x(this, activity, binderC0672b));
        Bundle B4 = binderC0672b.B(50L);
        if (B4 != null) {
            bundle.putAll(B4);
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStarted(Activity activity) {
        this.f4782j.b(new t(this, activity));
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStopped(Activity activity) {
        this.f4782j.b(new n(this, activity));
    }
}
