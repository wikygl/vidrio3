package J0;

import C0.i;
import S0.C0284u0;
import android.content.Intent;
import android.content.IntentFilter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends c<Boolean> {

    /* renamed from: i  reason: collision with root package name */
    public static final String f1195i = i.e("BatteryNotLowTracker");

    @Override // J0.d
    public final Object a() {
        Intent registerReceiver = this.f1200b.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        boolean z4 = false;
        if (registerReceiver == null) {
            i.c().b(f1195i, "getInitialState - null intent received", new Throwable[0]);
            return null;
        }
        return Boolean.valueOf((registerReceiver.getIntExtra("status", -1) == 1 || ((float) registerReceiver.getIntExtra("level", -1)) / ((float) registerReceiver.getIntExtra("scale", -1)) > 0.15f) ? true : true);
    }

    @Override // J0.c
    public final IntentFilter f() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_OKAY");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        return intentFilter;
    }

    @Override // J0.c
    public final void g(Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        i.c().a(f1195i, C0284u0.c("Received ", intent.getAction()), new Throwable[0]);
        String action = intent.getAction();
        action.getClass();
        if (!action.equals("android.intent.action.BATTERY_OKAY")) {
            if (action.equals("android.intent.action.BATTERY_LOW")) {
                c(Boolean.FALSE);
                return;
            }
            return;
        }
        c(Boolean.TRUE);
    }
}
