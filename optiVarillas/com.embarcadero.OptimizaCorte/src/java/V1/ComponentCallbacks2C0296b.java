package V1;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: V1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class ComponentCallbacks2C0296b implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    /* renamed from: n  reason: collision with root package name */
    public static final ComponentCallbacks2C0296b f2566n = new ComponentCallbacks2C0296b();

    /* renamed from: j  reason: collision with root package name */
    public final AtomicBoolean f2567j = new AtomicBoolean();

    /* renamed from: k  reason: collision with root package name */
    public final AtomicBoolean f2568k = new AtomicBoolean();

    /* renamed from: l  reason: collision with root package name */
    public final ArrayList f2569l = new ArrayList();

    /* renamed from: m  reason: collision with root package name */
    public boolean f2570m = false;

    /* renamed from: V1.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface a {
        void a(boolean z4);
    }

    public final void a(r rVar) {
        synchronized (f2566n) {
            this.f2569l.add(rVar);
        }
    }

    public final void b(boolean z4) {
        synchronized (f2566n) {
            try {
                Iterator it = this.f2569l.iterator();
                while (it.hasNext()) {
                    ((a) it.next()).a(z4);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityCreated(Activity activity, Bundle bundle) {
        AtomicBoolean atomicBoolean = this.f2568k;
        boolean compareAndSet = this.f2567j.compareAndSet(true, false);
        atomicBoolean.set(true);
        if (compareAndSet) {
            b(false);
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityResumed(Activity activity) {
        AtomicBoolean atomicBoolean = this.f2568k;
        boolean compareAndSet = this.f2567j.compareAndSet(true, false);
        atomicBoolean.set(true);
        if (compareAndSet) {
            b(false);
        }
    }

    @Override // android.content.ComponentCallbacks2
    public final void onTrimMemory(int i4) {
        if (i4 == 20 && this.f2567j.compareAndSet(false, true)) {
            this.f2568k.set(true);
            b(true);
        }
    }

    @Override // android.content.ComponentCallbacks
    public final void onLowMemory() {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStopped(Activity activity) {
    }

    @Override // android.content.ComponentCallbacks
    public final void onConfigurationChanged(Configuration configuration) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }
}
