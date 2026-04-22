package D1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class q0 extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ t0 f762a;

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        boolean equals = "android.intent.action.USER_PRESENT".equals(intent.getAction());
        t0 t0Var = this.f762a;
        if (equals) {
            t0Var.f779e = true;
        } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
            t0Var.f779e = false;
        }
    }
}
