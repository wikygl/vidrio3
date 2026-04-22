package D1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.vb;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class Z {

    /* renamed from: d  reason: collision with root package name */
    public boolean f664d;

    /* renamed from: e  reason: collision with root package name */
    public Context f665e;

    /* renamed from: c  reason: collision with root package name */
    public boolean f663c = false;

    /* renamed from: b  reason: collision with root package name */
    public final WeakHashMap f662b = new WeakHashMap();

    /* renamed from: a  reason: collision with root package name */
    public final Y f661a = new Y(0, this);

    @SuppressLint({"UnprotectedReceiver"})
    public final synchronized void a(Context context) {
        try {
            if (this.f663c) {
                return;
            }
            Context applicationContext = context.getApplicationContext();
            this.f665e = applicationContext;
            if (applicationContext == null) {
                this.f665e = context;
            }
            Gb.a(this.f665e);
            vb vbVar = Gb.q3;
            A1.r rVar = A1.r.f168d;
            this.f664d = ((Boolean) rVar.f171c.a(vbVar)).booleanValue();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            if (((Boolean) rVar.f171c.a(Gb.J9)).booleanValue() && Build.VERSION.SDK_INT >= 33) {
                this.f665e.registerReceiver(this.f661a, intentFilter, 4);
            } else {
                this.f665e.registerReceiver(this.f661a, intentFilter);
            }
            this.f663c = true;
        } catch (Throwable th) {
            throw th;
        }
    }

    public final synchronized void b(Context context, BroadcastReceiver broadcastReceiver) {
        if (this.f664d) {
            this.f662b.remove(broadcastReceiver);
        } else {
            context.unregisterReceiver(broadcastReceiver);
        }
    }
}
