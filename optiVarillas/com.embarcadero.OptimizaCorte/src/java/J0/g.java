package J0;

import C0.i;
import S0.C0284u0;
import android.content.Intent;
import android.content.IntentFilter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g extends c<Boolean> {

    /* renamed from: i  reason: collision with root package name */
    public static final String f1212i = i.e("StorageNotLowTracker");

    @Override // J0.d
    public final Object a() {
        Intent registerReceiver = this.f1200b.registerReceiver(null, f());
        if (registerReceiver != null && registerReceiver.getAction() != null) {
            String action = registerReceiver.getAction();
            action.getClass();
            if (!action.equals("android.intent.action.DEVICE_STORAGE_LOW")) {
                if (!action.equals("android.intent.action.DEVICE_STORAGE_OK")) {
                    return null;
                }
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override // J0.c
    public final IntentFilter f() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
        intentFilter.addAction("android.intent.action.DEVICE_STORAGE_LOW");
        return intentFilter;
    }

    @Override // J0.c
    public final void g(Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        i.c().a(f1212i, C0284u0.c("Received ", intent.getAction()), new Throwable[0]);
        String action = intent.getAction();
        action.getClass();
        if (!action.equals("android.intent.action.DEVICE_STORAGE_LOW")) {
            if (action.equals("android.intent.action.DEVICE_STORAGE_OK")) {
                c(Boolean.TRUE);
                return;
            }
            return;
        }
        c(Boolean.FALSE);
    }
}
