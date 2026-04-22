package f2;

import D1.RunnableC0184e;
import D1.g0;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import p2.AbstractC0757f;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class g implements Q1.a {

    /* renamed from: c  reason: collision with root package name */
    public static g f3401c;

    /* renamed from: a  reason: collision with root package name */
    public final Context f3402a;

    /* renamed from: b  reason: collision with root package name */
    public final ExecutorService f3403b;

    public g(Context context) {
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        this.f3403b = Executors.newSingleThreadExecutor();
        this.f3402a = context;
        newSingleThreadScheduledExecutor.scheduleAtFixedRate(new RunnableC0184e(1, this), 0L, 86400L, TimeUnit.SECONDS);
    }

    public static final SharedPreferences b(Context context) {
        return context.getSharedPreferences("app_set_id_storage", 0);
    }

    public static final void c(Context context) {
        String str;
        SharedPreferences b4 = b(context);
        if (!b4.edit().putLong("app_set_id_last_used_time", System.currentTimeMillis()).commit()) {
            String valueOf = String.valueOf(context.getPackageName());
            if (valueOf.length() != 0) {
                str = "Failed to store app set ID last used time for App ".concat(valueOf);
            } else {
                str = new String("Failed to store app set ID last used time for App ");
            }
            Log.e("AppSet", str);
            throw new Exception("Failed to store the app set ID last used time.");
        }
    }

    @Override // Q1.a
    public final AbstractC0757f<Q1.b> a() {
        C0758g c0758g = new C0758g();
        this.f3403b.execute(new g0(this, 8, c0758g));
        return c0758g.f5552a;
    }
}
