package c2;

import T1.i;
import W1.C0324l;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class c<T> {

    /* renamed from: a  reason: collision with root package name */
    public final String f2945a;

    /* renamed from: b  reason: collision with root package name */
    public Object f2946b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a extends Exception {
    }

    public c(String str) {
        this.f2945a = str;
    }

    public abstract T a(IBinder iBinder);

    public final T b(Context context) {
        Context context2;
        if (this.f2946b == null) {
            C0324l.d(context);
            AtomicBoolean atomicBoolean = i.f2356a;
            try {
                context2 = context.createPackageContext("com.google.android.gms", 3);
            } catch (PackageManager.NameNotFoundException unused) {
                context2 = null;
            }
            if (context2 != null) {
                try {
                    this.f2946b = a((IBinder) context2.getClassLoader().loadClass(this.f2945a).newInstance());
                } catch (ClassNotFoundException e4) {
                    throw new Exception("Could not load creator class.", e4);
                } catch (IllegalAccessException e5) {
                    throw new Exception("Could not access creator.", e5);
                } catch (InstantiationException e6) {
                    throw new Exception("Could not instantiate creator.", e6);
                }
            } else {
                throw new Exception("Could not get remote context.");
            }
        }
        return (T) this.f2946b;
    }
}
