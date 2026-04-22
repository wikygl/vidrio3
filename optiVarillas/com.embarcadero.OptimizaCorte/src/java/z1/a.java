package Z1;

import W1.C0324l;
import W1.Z;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import b2.c;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class a {

    /* renamed from: b  reason: collision with root package name */
    public static final Object f2840b = new Object();

    /* renamed from: c  reason: collision with root package name */
    public static volatile a f2841c;

    /* renamed from: a  reason: collision with root package name */
    public final ConcurrentHashMap f2842a = new ConcurrentHashMap();

    public static a a() {
        if (f2841c == null) {
            synchronized (f2840b) {
                try {
                    if (f2841c == null) {
                        f2841c = new a();
                    }
                } finally {
                }
            }
        }
        a aVar = f2841c;
        C0324l.d(aVar);
        return aVar;
    }

    public final void b(Context context, ServiceConnection serviceConnection) {
        if (!(serviceConnection instanceof Z)) {
            ConcurrentHashMap concurrentHashMap = this.f2842a;
            if (concurrentHashMap.containsKey(serviceConnection)) {
                try {
                    try {
                        context.unbindService((ServiceConnection) concurrentHashMap.get(serviceConnection));
                    } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException unused) {
                    }
                    return;
                } finally {
                    concurrentHashMap.remove(serviceConnection);
                }
            }
        }
        try {
            context.unbindService(serviceConnection);
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException unused2) {
        }
    }

    public final boolean c(Context context, String str, Intent intent, ServiceConnection serviceConnection, int i4, Executor executor) {
        boolean bindService;
        boolean bindService2;
        ComponentName component = intent.getComponent();
        if (component != null) {
            String packageName = component.getPackageName();
            "com.google.android.gms".equals(packageName);
            try {
                if ((c.a(context).a(packageName, 0).flags & 2097152) != 0) {
                    Log.w("ConnectionTracker", "Attempted to bind to a service in a STOPPED package.");
                    return false;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        if (!(serviceConnection instanceof Z)) {
            ConcurrentHashMap concurrentHashMap = this.f2842a;
            ServiceConnection serviceConnection2 = (ServiceConnection) concurrentHashMap.putIfAbsent(serviceConnection, serviceConnection);
            if (serviceConnection2 != null && serviceConnection != serviceConnection2) {
                Log.w("ConnectionTracker", String.format("Duplicate binding with the same ServiceConnection: %s, %s, %s.", serviceConnection, str, intent.getAction()));
            }
            if (executor == null) {
                executor = null;
            }
            try {
                if (Build.VERSION.SDK_INT >= 29 && executor != null) {
                    bindService2 = context.bindService(intent, i4, executor, serviceConnection);
                } else {
                    bindService2 = context.bindService(intent, serviceConnection, i4);
                }
                if (!bindService2) {
                    return false;
                }
                return bindService2;
            } finally {
                concurrentHashMap.remove(serviceConnection, serviceConnection);
            }
        }
        if (executor == null) {
            executor = null;
        }
        if (Build.VERSION.SDK_INT >= 29 && executor != null) {
            bindService = context.bindService(intent, i4, executor, serviceConnection);
            return bindService;
        }
        return context.bindService(intent, serviceConnection, i4);
    }
}
