package J0;

import C0.i;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class f extends d<H0.b> {

    /* renamed from: j  reason: collision with root package name */
    public static final String f1206j = i.e("NetworkStateTracker");

    /* renamed from: g  reason: collision with root package name */
    public final ConnectivityManager f1207g;

    /* renamed from: h  reason: collision with root package name */
    public final b f1208h;

    /* renamed from: i  reason: collision with root package name */
    public final a f1209i;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class a extends BroadcastReceiver {
        public a() {
        }

        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                i.c().a(f.f1206j, "Network broadcast received", new Throwable[0]);
                f fVar = f.this;
                fVar.c(fVar.f());
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class b extends ConnectivityManager.NetworkCallback {
        public b() {
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            i c4 = i.c();
            String str = f.f1206j;
            c4.a(str, "Network capabilities changed: " + networkCapabilities, new Throwable[0]);
            f fVar = f.this;
            fVar.c(fVar.f());
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onLost(Network network) {
            i.c().a(f.f1206j, "Network connection lost", new Throwable[0]);
            f fVar = f.this;
            fVar.c(fVar.f());
        }
    }

    public f(Context context, O0.a aVar) {
        super(context, aVar);
        this.f1207g = (ConnectivityManager) this.f1200b.getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 24) {
            this.f1208h = new b();
        } else {
            this.f1209i = new a();
        }
    }

    @Override // J0.d
    public final H0.b a() {
        return f();
    }

    @Override // J0.d
    public final void d() {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 24) {
            z4 = true;
        } else {
            z4 = false;
        }
        String str = f1206j;
        if (z4) {
            try {
                i.c().a(str, "Registering network callback", new Throwable[0]);
                this.f1207g.registerDefaultNetworkCallback(this.f1208h);
                return;
            } catch (IllegalArgumentException | SecurityException e4) {
                i.c().b(str, "Received exception while registering network callback", e4);
                return;
            }
        }
        i.c().a(str, "Registering broadcast receiver", new Throwable[0]);
        this.f1200b.registerReceiver(this.f1209i, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override // J0.d
    public final void e() {
        boolean z4;
        if (Build.VERSION.SDK_INT >= 24) {
            z4 = true;
        } else {
            z4 = false;
        }
        String str = f1206j;
        if (z4) {
            try {
                i.c().a(str, "Unregistering network callback", new Throwable[0]);
                this.f1207g.unregisterNetworkCallback(this.f1208h);
                return;
            } catch (IllegalArgumentException | SecurityException e4) {
                i.c().b(str, "Received exception while unregistering network callback", e4);
                return;
            }
        }
        i.c().a(str, "Unregistering broadcast receiver", new Throwable[0]);
        this.f1200b.unregisterReceiver(this.f1209i);
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [java.lang.Object, H0.b] */
    public final H0.b f() {
        boolean z4;
        Network activeNetwork;
        NetworkCapabilities networkCapabilities;
        boolean z5;
        ConnectivityManager connectivityManager = this.f1207g;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean z6 = false;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                activeNetwork = connectivityManager.getActiveNetwork();
                networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            } catch (SecurityException e4) {
                i.c().b(f1206j, "Unable to validate active network", e4);
            }
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(16)) {
                    z5 = true;
                    boolean isActiveNetworkMetered = connectivityManager.isActiveNetworkMetered();
                    if (activeNetworkInfo != null && !activeNetworkInfo.isRoaming()) {
                        z6 = true;
                    }
                    ?? obj = new Object();
                    obj.f1012a = z4;
                    obj.f1013b = z5;
                    obj.f1014c = isActiveNetworkMetered;
                    obj.f1015d = z6;
                    return obj;
                }
            }
        }
        z5 = false;
        boolean isActiveNetworkMetered2 = connectivityManager.isActiveNetworkMetered();
        if (activeNetworkInfo != null) {
            z6 = true;
        }
        ?? obj2 = new Object();
        obj2.f1012a = z4;
        obj2.f1013b = z5;
        obj2.f1014c = isActiveNetworkMetered2;
        obj2.f1015d = z6;
        return obj2;
    }
}
