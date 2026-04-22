package S0;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import java.util.concurrent.Executor;

/* renamed from: S0.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class RunnableC0281t implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2281j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2282k;

    public /* synthetic */ RunnableC0281t(int i4, Object obj) {
        this.f2281j = i4;
        this.f2282k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [java.util.concurrent.Executor, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio] */
    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2281j) {
            case 0:
                ActivityInicio.b bVar = (ActivityInicio.b) this.f2282k;
                bVar.getClass();
                ?? r22 = ActivityInicio.this;
                new WebView(r22);
                new Handler().postDelayed(new RunnableC0283u(0, bVar), 2000L);
                r22.f2986u0.f2897x = false;
                r22.f2959S.setText("Version 5.2.9.2 (build 317) FREE");
                r22.f2967a0.setVisibility(0);
                r22.l0.setVisibility(0);
                Log.d("INI_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            default:
                androidx.profileinstaller.c.b((Context) this.f2282k, (Executor) new Object(), androidx.profileinstaller.c.a, false);
                return;
        }
    }
}
