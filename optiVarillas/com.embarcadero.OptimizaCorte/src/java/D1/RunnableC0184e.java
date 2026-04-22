package D1;

import android.content.Context;
import android.util.Log;

/* renamed from: D1.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class RunnableC0184e implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f678j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f679k;

    public /* synthetic */ RunnableC0184e(int i4, Object obj) {
        this.f678j = i4;
        this.f679k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        long j4;
        String str;
        String str2;
        switch (this.f678j) {
            case 0:
                C0195o c0195o = (C0195o) this.f679k;
                c0195o.c(c0195o.f745a);
                return;
            default:
                f2.g gVar = (f2.g) this.f679k;
                long j5 = f2.g.b(gVar.f3402a).getLong("app_set_id_last_used_time", -1L);
                if (j5 != -1) {
                    j4 = j5 + 33696000000L;
                } else {
                    j4 = -1;
                }
                if (j4 != -1 && System.currentTimeMillis() > j4) {
                    Context context = gVar.f3402a;
                    if (!f2.g.b(context).edit().remove("app_set_id").commit()) {
                        String valueOf = String.valueOf(context.getPackageName());
                        if (valueOf.length() != 0) {
                            str2 = "Failed to clear app set ID generated for App ".concat(valueOf);
                        } else {
                            str2 = new String("Failed to clear app set ID generated for App ");
                        }
                        Log.e("AppSet", str2);
                    }
                    if (!context.getSharedPreferences("app_set_id_storage", 0).edit().remove("app_set_id_last_used_time").commit()) {
                        String valueOf2 = String.valueOf(context.getPackageName());
                        if (valueOf2.length() != 0) {
                            str = "Failed to clear app set ID last used time for App ".concat(valueOf2);
                        } else {
                            str = new String("Failed to clear app set ID last used time for App ");
                        }
                        Log.e("AppSet", str);
                        return;
                    }
                    return;
                }
                return;
        }
    }
}
