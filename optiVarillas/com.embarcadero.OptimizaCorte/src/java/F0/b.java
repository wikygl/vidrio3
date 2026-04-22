package F0;

import C0.i;
import D0.d;
import L0.p;
import S0.C0284u0;
import android.content.Context;
import android.content.Intent;
import androidx.work.impl.background.systemalarm.SystemAlarmService;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b implements d {

    /* renamed from: k  reason: collision with root package name */
    public static final String f899k = i.e("SystemAlarmScheduler");

    /* renamed from: j  reason: collision with root package name */
    public final Context f900j;

    public b(Context context) {
        this.f900j = context.getApplicationContext();
    }

    @Override // D0.d
    public final void b(String str) {
        String str2 = androidx.work.impl.background.systemalarm.a.m;
        Context context = this.f900j;
        Intent intent = new Intent(context, SystemAlarmService.class);
        intent.setAction("ACTION_STOP_WORK");
        intent.putExtra("KEY_WORKSPEC_ID", str);
        context.startService(intent);
    }

    @Override // D0.d
    public final boolean e() {
        return true;
    }

    @Override // D0.d
    public final void f(p... pVarArr) {
        for (p pVar : pVarArr) {
            i.c().a(f899k, C0284u0.c("Scheduling work with workSpecId ", pVar.f1450a), new Throwable[0]);
            String str = pVar.f1450a;
            Context context = this.f900j;
            context.startService(androidx.work.impl.background.systemalarm.a.c(context, str));
        }
    }
}
