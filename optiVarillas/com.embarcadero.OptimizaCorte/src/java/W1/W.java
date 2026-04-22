package W1;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import java.util.HashMap;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class W implements ServiceConnection, Z {

    /* renamed from: a  reason: collision with root package name */
    public final HashMap f2666a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    public int f2667b = 2;

    /* renamed from: c  reason: collision with root package name */
    public boolean f2668c;

    /* renamed from: d  reason: collision with root package name */
    public IBinder f2669d;

    /* renamed from: e  reason: collision with root package name */
    public final V f2670e;
    public ComponentName f;

    /* renamed from: g  reason: collision with root package name */
    public final /* synthetic */ Y f2671g;

    public W(Y y4, V v4) {
        this.f2671g = y4;
        this.f2670e = v4;
    }

    public final void a(String str, Executor executor) {
        StrictMode.VmPolicy.Builder permitUnsafeIntentLaunch;
        this.f2667b = 3;
        StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
        if (Build.VERSION.SDK_INT >= 31) {
            permitUnsafeIntentLaunch = new StrictMode.VmPolicy.Builder(vmPolicy).permitUnsafeIntentLaunch();
            StrictMode.setVmPolicy(permitUnsafeIntentLaunch.build());
        }
        try {
            Y y4 = this.f2671g;
            Z1.a aVar = y4.f2675g;
            Context context = y4.f2674e;
            boolean c4 = aVar.c(context, str, this.f2670e.a(context), this, 4225, executor);
            this.f2668c = c4;
            if (c4) {
                this.f2671g.f.sendMessageDelayed(this.f2671g.f.obtainMessage(1, this.f2670e), this.f2671g.f2677i);
            } else {
                this.f2667b = 2;
                try {
                    Y y5 = this.f2671g;
                    y5.f2675g.b(y5.f2674e, this);
                } catch (IllegalArgumentException unused) {
                }
            }
            StrictMode.setVmPolicy(vmPolicy);
        } catch (Throwable th) {
            StrictMode.setVmPolicy(vmPolicy);
            throw th;
        }
    }

    @Override // android.content.ServiceConnection
    public final void onBindingDied(ComponentName componentName) {
        onServiceDisconnected(componentName);
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this.f2671g.f2673d) {
            try {
                this.f2671g.f.removeMessages(1, this.f2670e);
                this.f2669d = iBinder;
                this.f = componentName;
                for (ServiceConnection serviceConnection : this.f2666a.values()) {
                    serviceConnection.onServiceConnected(componentName, iBinder);
                }
                this.f2667b = 1;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        synchronized (this.f2671g.f2673d) {
            try {
                this.f2671g.f.removeMessages(1, this.f2670e);
                this.f2669d = null;
                this.f = componentName;
                for (ServiceConnection serviceConnection : this.f2666a.values()) {
                    serviceConnection.onServiceDisconnected(componentName);
                }
                this.f2667b = 2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
