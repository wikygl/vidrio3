package J0;

import C0.i;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class c<T> extends d<T> {

    /* renamed from: h  reason: collision with root package name */
    public static final String f1196h = i.e("BrdcstRcvrCnstrntTrckr");

    /* renamed from: g  reason: collision with root package name */
    public final a f1197g;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class a extends BroadcastReceiver {
        public a() {
        }

        @Override // android.content.BroadcastReceiver
        public final void onReceive(Context context, Intent intent) {
            if (intent != null) {
                c.this.g(intent);
            }
        }
    }

    public c(Context context, O0.a aVar) {
        super(context, aVar);
        this.f1197g = new a();
    }

    @Override // J0.d
    public final void d() {
        i.c().a(f1196h, getClass().getSimpleName().concat(": registering receiver"), new Throwable[0]);
        this.f1200b.registerReceiver(this.f1197g, f());
    }

    @Override // J0.d
    public final void e() {
        i.c().a(f1196h, getClass().getSimpleName().concat(": unregistering receiver"), new Throwable[0]);
        this.f1200b.unregisterReceiver(this.f1197g);
    }

    public abstract IntentFilter f();

    public abstract void g(Intent intent);
}
