package F0;

import C0.i;
import D0.k;
import L0.g;
import M0.f;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.work.impl.WorkDatabase;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public static final String f898a = i.e("Alarms");

    public static void a(int i4, Context context, String str) {
        int i5;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        Intent b4 = androidx.work.impl.background.systemalarm.a.b(context, str);
        if (Build.VERSION.SDK_INT >= 23) {
            i5 = 603979776;
        } else {
            i5 = 536870912;
        }
        PendingIntent service = PendingIntent.getService(context, i4, b4, i5);
        if (service != null && alarmManager != null) {
            i c4 = i.c();
            c4.a(f898a, "Cancelling existing alarm with (workSpecId, systemId) (" + str + ", " + i4 + ")", new Throwable[0]);
            alarmManager.cancel(service);
        }
    }

    public static void b(Context context, k kVar, String str, long j4) {
        int a4;
        WorkDatabase workDatabase = kVar.f587c;
        L0.i iVar = (L0.i) workDatabase.k();
        g a5 = iVar.a(str);
        if (a5 != null) {
            a(a5.f1436b, context, str);
            c(context, str, a5.f1436b, j4);
            return;
        }
        f fVar = new f(workDatabase);
        synchronized (f.class) {
            a4 = fVar.a("next_alarm_manager_id");
        }
        iVar.b(new g(str, a4));
        c(context, str, a4, j4);
    }

    public static void c(Context context, String str, int i4, long j4) {
        int i5;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        if (Build.VERSION.SDK_INT >= 23) {
            i5 = 201326592;
        } else {
            i5 = 134217728;
        }
        PendingIntent service = PendingIntent.getService(context, i4, androidx.work.impl.background.systemalarm.a.b(context, str), i5);
        if (alarmManager != null) {
            alarmManager.setExact(0, j4, service);
        }
    }
}
