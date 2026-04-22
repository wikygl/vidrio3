package i2;

import X2.a;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/* renamed from: i2.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0464j implements Application.ActivityLifecycleCallbacks {

    /* renamed from: j  reason: collision with root package name */
    public final Activity f3765j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0467m f3766k;

    public C0464j(C0467m c0467m, Activity activity) {
        this.f3766k = c0467m;
        this.f3765j = activity;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityDestroyed(Activity activity) {
        if (activity != this.f3765j) {
            return;
        }
        b0 b0Var = new b0("Activity is destroyed.", 3);
        C0467m c0467m = this.f3766k;
        c0467m.b();
        a.InterfaceC0034a interfaceC0034a = (a.InterfaceC0034a) c0467m.f3780j.getAndSet(null);
        if (interfaceC0034a != null) {
            b0Var.a();
            interfaceC0034a.a();
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStopped(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }
}
