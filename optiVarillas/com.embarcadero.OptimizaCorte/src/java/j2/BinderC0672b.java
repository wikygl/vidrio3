package j2;

import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j2.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class BinderC0672b extends AbstractBinderC0676f {

    /* renamed from: k  reason: collision with root package name */
    public final AtomicReference f4792k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f4793l;

    public BinderC0672b() {
        super(1);
        attachInterface(this, "com.google.android.gms.measurement.api.internal.IBundleReceiver");
        this.f4792k = new AtomicReference();
    }

    public static final Object p0(Bundle bundle, Class cls) {
        Object obj;
        if (bundle == null || (obj = bundle.get("r")) == null) {
            return null;
        }
        try {
            return cls.cast(obj);
        } catch (ClassCastException e4) {
            String canonicalName = cls.getCanonicalName();
            String canonicalName2 = obj.getClass().getCanonicalName();
            Log.w("AM", "Unexpected object type. Expected, Received: " + canonicalName + ", " + canonicalName2, e4);
            throw e4;
        }
    }

    public final Bundle B(long j4) {
        Bundle bundle;
        synchronized (this.f4792k) {
            if (!this.f4793l) {
                try {
                    this.f4792k.wait(j4);
                } catch (InterruptedException unused) {
                    return null;
                }
            }
            bundle = (Bundle) this.f4792k.get();
        }
        return bundle;
    }

    public final void Z(Bundle bundle) {
        synchronized (this.f4792k) {
            try {
                this.f4792k.set(bundle);
                this.f4793l = true;
                this.f4792k.notify();
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
